package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.AutomaticPermitDTO;
import com.coppel.client.customcomplements.dto.AutomaticPermitRequest;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.ImpiRequest;
import com.coppel.client.customcomplements.dto.PermitColorRangeDTO;
import com.coppel.client.customcomplements.dto.TariffsDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.request.customcomplements.AutomaticPermitCheckRequest;
import com.coppel.dto.request.customcomplements.CustomsInfoRequest;
import com.coppel.dto.request.customcomplements.FractionCheckRequest;
import com.coppel.dto.response.customcomplements.AutomaticPermitResultDTO;
import com.coppel.dto.response.customcomplements.CustomsInfoResultDTO;
import com.coppel.dto.response.customcomplements.FractionCheckDTO;
import com.coppel.dto.response.customcomplements.FractionRulesDTO;
import com.coppel.exceptions.customcomplements.BadRequestException;
import com.coppel.exceptions.customcomplements.NotFoundException;
import com.coppel.services.customcomplements.CustomsInfoService;
import com.coppel.services.customcomplements.impl.CustomsRules.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.coppel.client.customcomplements.PsResponses.data;
import static com.coppel.client.customcomplements.PsResponses.list;
import static com.coppel.services.customcomplements.impl.CustomsRules.blank;
import static com.coppel.services.customcomplements.impl.CustomsRules.positive;

@Service
@RequiredArgsConstructor
public class CustomsInfoServiceImpl implements CustomsInfoService {

    private final CustomComplementsPsClient psClient;
    private final FractionRulesResolver fractionRules;

    /**
     * Antes ValidaCampo(TXT_FRACCION): máscara completa, fracción activa en el catálogo, IMPI de la fracción anterior y
     * aranceles para el país.
     */
    @Override
    public FractionCheckDTO checkFraction(FractionCheckRequest request) {
        String fraction = validMxnFraction(request.fraction());
        FractionRulesDTO rules = existingRules(fraction);
        boolean impiExists = !blank(request.previousFraction()) && !fraction.equals(request.previousFraction().trim())
            && data(psClient.impiExists(request.area(), request.code(),
                new ImpiRequest("0", request.piece(), request.sizeRange(), request.previousFraction().trim()))).exists();
        TariffsDTO tariffs = request.countryId() == null || request.countryId() <= 0
            ? new TariffsDTO("0.00", "0.00", CustomsRules.NOT_APPLICABLE, CustomsRules.NOT_APPLICABLE, false)
            : data(psClient.tariffs(fraction, request.countryId()));
        return new FractionCheckDTO(rules, tariffs, impiExists);
    }

    /** Antes ObtenerDatosAranceles (al cambiar el país o la fracción). */
    @Override
    public TariffsDTO getTariffs(String fraction, int countryId) {
        return data(psClient.tariffs(validMxnFraction(fraction), countryId));
    }

    /** Antes ValidaCampo(CMB_FRACCIONUSA). */
    @Override
    public void validateUsaFraction(String fraction) {
        if (!CustomsRules.matchesMask(fraction == null ? null : fraction.trim(), CustomsRules.USA_FRACTION_MASK)) {
            throw new BadRequestException(AppMessages.CAPTURAR_FRACCION_USA);
        }
        if (!data(psClient.validateUsa(fraction.trim())).exists()) {
            throw new NotFoundException(AppMessages.FRACCION_USA_NO_REGISTRADA);
        }
    }

    /**
     * Antes AduanasViewModel.ValidarPermisoAutomatico: sólo con fracción completa, UMC, costo unitario y valor UMT;
     * si falta algo el escritorio no recalculaba (aquí regresa sin permiso).
     */
    @Override
    public AutomaticPermitResultDTO automaticPermit(AutomaticPermitCheckRequest request) {
        List<PermitColorRangeDTO> ranges = list(psClient.permitColorRanges());
        AutomaticPermitDTO permit = permit(request.code(), request.fraction(), request.commercialUnitId(),
            request.unitCost(), request.tariffValue(), request.pieces());
        return new AutomaticPermitResultDTO(permit.required(), permit.percentage(), permit.price(),
            CustomsRules.color(permit.percentage(), ranges));
    }

    /**
     * Antes Aceptar de FrmAduanas: valida en el orden de la pantalla, vuelve a consultar lo que dependía de la base
     * (fracciones, aranceles, permiso automático) y calcula lo derivado (peso en kg, piezas, cuota compensatoria,
     * arancel a aplicar, color del %).
     */
    @Override
    public CustomsInfoResultDTO accept(CustomsInfoRequest request) {
        CustomsRowDTO row = request.row();
        String fraction = validMxnFraction(row.fraction());
        FractionRulesDTO rules = existingRules(fraction);
        TariffsDTO tariffs = row.countryId() > 0 ? data(psClient.tariffs(fraction, row.countryId())) : null;
        List<CatalogItemDTO> quotaTypes = list(psClient.quotaTypes());
        boolean perPiece = quotaTypes.stream()
            .anyMatch(type -> type.id().equals(String.valueOf(row.quotaTypeId()))
                && CustomsRules.QUOTA_PER_PIECE.equals(type.name().trim()));

        CustomsRules.validate(row, new Context(request.area().isClothing(),
            tariffs != null && tariffs.hasPreferential(), perPiece));
        validateUsaFraction(row.usaFraction());

        int pieces = CustomsRules.componentPieces(row.commercialUnitId(), rules.tariffUnitId(), row.componentPieces());
        BigDecimal quota = row.compensatoryQuota()
            ? CustomsRules.compensatoryQuota(perPiece, perPiece && row.specificReferenceCost(), row.referenceCost(),
                row.unitCost(), pieces, row.tariffValue())
            : BigDecimal.ZERO;
        if (quota.compareTo(CustomsRules.MAX_QUOTA) > 0) {
            throw new BadRequestException(AppMessages.CUOTA_MAXIMA);
        }
        boolean preferential = tariffs != null && tariffs.hasPreferential() && row.preferentialTariff();
        BigDecimal normalFrontier = tariffs == null ? row.normalFrontierTariff() : CustomsRules.decimal(tariffs.normalFrontier());
        BigDecimal normalInterior = tariffs == null ? row.normalInteriorTariff() : CustomsRules.decimal(tariffs.normalInterior());
        String preferentialFrontier = tariffs == null ? CustomsRules.NOT_APPLICABLE : tariffs.preferentialFrontier();
        String preferentialInterior = tariffs == null ? CustomsRules.NOT_APPLICABLE : tariffs.preferentialInterior();

        AutomaticPermitDTO permit = permit(request.code(), fraction, row.commercialUnitId(), row.unitCost(),
            row.tariffValue(), pieces);
        String color = CustomsRules.color(permit.percentage(), list(psClient.permitColorRanges()));
        boolean producer = row.tmec() && row.producer();

        CustomsRowDTO normalized = new CustomsRowDTO(row.piece(), row.sizeRange(), fraction, row.appliesDecree(),
            row.customsDescription().trim(), text(row.composition()), text(row.complement()),
            row.englishDescription().trim(), row.countryId(), text(row.countryName()), row.usaFraction().trim(),
            row.unitCost(), permit.price(), permit.required(), permit.percentage(), row.weightUnitId(), row.weight(),
            CustomsRules.weightInKg(row.weightUnitId(), row.weight()), rules.tariffUnitId(), row.commercialUnitId(),
            pieces, row.tariffValue(), rules.containsSugar() ? zero(row.sugarKg()) : BigDecimal.ZERO,
            row.compensatoryQuota(), row.compensatoryQuota() ? row.quotaTypeId() : 0,
            row.compensatoryQuota() ? zero(row.referenceCost()) : BigDecimal.ZERO,
            row.compensatoryQuota() && perPiece && row.specificReferenceCost(),
            row.compensatoryQuota() && row.quotaToPay(), quota, preferential,
            preferential ? 0 : row.nonTariffCauseId(), row.tmec(), producer,
            row.tmec() && !producer ? row.producerTypeId() : 0, row.tmec() ? row.preferentialCriterionId() : 0,
            normalFrontier, normalInterior, preferential ? CustomsRules.decimal(preferentialFrontier) : BigDecimal.ZERO,
            preferential ? CustomsRules.decimal(preferentialInterior) : BigDecimal.ZERO,
            CustomsRules.applyTariff(normalFrontier, preferentialFrontier, preferential),
            CustomsRules.applyTariff(normalInterior, preferentialInterior, preferential), color, rules.sanitaryNotice());
        return new CustomsInfoResultDTO(normalized, rules);
    }

    private AutomaticPermitDTO permit(int code, String fraction, Integer commercialUnitId, BigDecimal unitCost,
                                      BigDecimal tariffValue, Integer pieces) {
        if (blank(fraction) || commercialUnitId == null || commercialUnitId <= 0 || !positive(unitCost)
            || !positive(tariffValue)) {
            return new AutomaticPermitDTO(false, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return data(psClient.automaticPermit(new AutomaticPermitRequest(code, fraction.trim(), commercialUnitId,
            unitCost, tariffValue, pieces == null ? 0 : pieces)));
    }

    /** Máscara completa (antes Contains(" ") o largo distinto); sin máscara configurada la pantalla no abría. */
    private String validMxnFraction(String value) {
        String mask = data(psClient.fractionMask()).mask();
        if (blank(mask)) {
            throw new BadRequestException(AppMessages.SIN_MASCARA);
        }
        String fraction = value == null ? "" : value.trim();
        if (!CustomsRules.matchesMask(fraction, mask)) {
            throw new BadRequestException(AppMessages.CAPTURAR_FRACCION_MXN);
        }
        return fraction;
    }

    private FractionRulesDTO existingRules(String fraction) {
        FractionRulesDTO rules = fractionRules.resolve(fraction);
        if (!rules.exists()) {
            throw new NotFoundException(AppMessages.FRACCION_NO_REGISTRADA);
        }
        return rules;
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private static BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
