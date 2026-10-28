package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.CentralDataDTO;
import com.coppel.client.customcomplements.dto.CentralSaveRequest;
import com.coppel.client.customcomplements.dto.CommitGeneralRequest;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.request.customcomplements.SaveComplementsRequest;
import com.coppel.dto.request.customcomplements.SaveHeaderInput;
import com.coppel.dto.response.customcomplements.ComponentDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coppel.services.customcomplements.impl.CustomsRules.blank;
import static com.coppel.services.customcomplements.impl.CustomsRules.require;

/**
 * Reglas de [F10] Guardar (antes FrmComplementosAduanales.Guardar, ValidaTodosLosCampos(TODOS), ValidarCosto,
 * ComplementosAduanalesDAL.GuardarComplementos y GrabarCentral).
 */
final class SaveRules {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    static final LocalDate EMPTY_DATE = ExpirationChecker.EMPTY_DATE;

    private SaveRules() {
    }

    /**
     * ValidaTodosLosCampos(TODOS) en el orden de la pantalla. {@code brandImageStatus} es el resultado de
     * fun_validarfechavencimiento para la fecha de la imagen de marca (antes ValidaFechaVencida).
     */
    static void validate(SaveComplementsRequest request, Map<String, CustomsRowDTO> customsByKey, LocalDate today,
                         ExpirationStatus brandImageStatus) {
        SaveHeaderInput header = request.header();
        require(header.importerId() != null && header.importerId() > 0, AppMessages.SELECCIONAR_IMPORTADORA);
        require(header.brandId() != null && header.brandId() > 0, AppMessages.SELECCIONAR_MARCA);
        require(!blank(header.model()), AppMessages.CAPTURAR_MODELO);
        if (header.brandImage()) {
            LocalDate expiration = header.brandImageExpiration();
            require(expiration != null && expiration.isAfter(today), AppMessages.FECHA_NO_VALIDA);
            require(brandImageStatus != ExpirationStatus.EXPIRED, AppMessages.FECHA_VENCIDA);
        }
        if (header.combined()) {
            require(header.componentCount() != null && header.componentCount() > 0, AppMessages.CAPTURAR_COMPONENTES);
            if (request.area().isClothing()) {
                require(header.componentCount() > 1, AppMessages.COMPONENTES_MAYOR_A_1);
            }
            int shown = SessionRules.componentCount(request.area(), request.components());
            require(header.componentCount() >= shown, AppMessages.COMPONENTES_MENOR);
            require(!blank(header.generalDescription()), AppMessages.CAPTURAR_DESCRIPCION_GENERAL);
        }
        boolean allCustoms = request.components().stream()
            .allMatch(component -> {
                CustomsRowDTO row = customsByKey.get(key(component.piece(), component.sizeRange()));
                return row != null && !blank(row.fraction());
            });
        require(allCustoms, AppMessages.INFORMACION_ADUANAL_COMPONENTES);
        if (request.area().isClothing()) {
            boolean representative = request.components().stream().anyMatch(ComponentDTO::representative);
            require(representative && header.representativeRange() > 0, AppMessages.SELECCIONAR_COMPONENTE_PRINCIPAL);
        }
    }

    /** Antes ValidarCosto: la suma de los costos unitarios (redondeada a 2) contra el costo general del código. */
    static boolean sameCost(SaveHeaderInput header, List<CustomsRowDTO> customs) {
        BigDecimal total = customs.stream()
            .map(row -> row.unitCost() == null ? BigDecimal.ZERO : row.unitCost())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal general = header.generalCost() == null ? BigDecimal.ZERO : header.generalCost();
        return general.setScale(2, RoundingMode.HALF_EVEN).compareTo(total) == 0;
    }

    /**
     * Parámetros de grabarcomplementos opción 0 (antes GuardarComplementos(0)): piezas que lo componen y cuota
     * compensatoria sumadas (la cuota truncada a 2 decimales).
     */
    static CommitGeneralRequest general(SaveComplementsRequest request, List<CustomsRowDTO> customs,
                                        boolean requiresSanitaryNotice) {
        SaveHeaderInput header = request.header();
        int pieces = customs.stream().mapToInt(CustomsRowDTO::componentPieces).sum();
        BigDecimal quota = customs.stream()
            .map(row -> row.quotaAmount() == null ? BigDecimal.ZERO : row.quotaAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.DOWN);
        return new CommitGeneralRequest(header.supplierId(), header.ftz(), value(header.importerId()),
            header.combined() ? text(header.generalDescription()) : "", value(header.brandId()), header.brandTypeId(),
            text(header.model()), header.brandImage(),
            header.brandImage() && header.brandImageExpiration() != null ? header.brandImageExpiration() : EMPTY_DATE,
            pieces, quota, header.pendingLabel(), header.sanitaryNoticeInProcess(), header.combined(),
            header.representativePiece(), header.representativeRange(), requiresSanitaryNotice);
    }

    /**
     * Aviso sanitario con la pieza / rango representativos y la fracción que lleva aviso (antes ActualizarRegulaciones;
     * grabarcomplementos opción 0 los vuelve a acomodar a la fracción representativa). Si el aviso está en trámite o
     * ya no se requiere, el SP los borra.
     */
    static List<RegulationRowDTO> sanitaryNotice(List<RegulationRowDTO> rows, SaveHeaderInput header, String fraction) {
        return rows.stream()
            .map(row -> new RegulationRowDTO(header.representativePiece(), header.representativeRange(), text(fraction),
                1, row.nomOrOffice(), row.certificate(), row.validity(), row.exceptionId(), row.nomInProcess()))
            .toList();
    }

    /**
     * Antes GrabarCentral. Muebles: aranceles y cuota × 100 como enteros. Ropa: el valor UMT sólo con unidad de tarifa
     * 4 (si no, 0) y redondeado a 2 decimales.
     */
    static CentralSaveRequest central(CustomsArea area, CentralDataDTO data) {
        if (area.isClothing()) {
            BigDecimal tariffValue = data.tariffUnit() == CustomsRules.TARIFF_UNIT_SQUARE_METERS
                ? data.tariffValue().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            return new CentralSaveRequest(data.fraction(), data.frontierTariff(), data.interiorTariff(),
                data.commercialUnit(), data.preferentialTariff(), data.producer(), data.producerType(),
                data.preferentialCriterion(), "", false, BigDecimal.ZERO, tariffValue, data.appliesDecree(),
                data.componentPieces(), data.weight(), data.usaFraction(), data.importBrand(), data.hasComponents(),
                data.permitPrice(), data.nomKey(), data.complianceNom(), data.nomCertificate(), data.appliesNom(),
                data.nomExpiration(), data.sanitaryNotice(), data.sanitaryNoticeExpiration(), data.labelingNom(),
                data.under24Volts());
        }
        return new CentralSaveRequest(data.fraction(), times100(data.frontierTariff()), times100(data.interiorTariff()),
            data.commercialUnit(), data.preferentialTariff(), data.producer(), data.producerType(),
            data.preferentialCriterion(), data.countryKey(), data.tmec(), times100(data.compensatoryQuota()),
            BigDecimal.ZERO, false, 0, BigDecimal.ZERO, "", 0, false, BigDecimal.ZERO, 0, 0, false, false, EMPTY_DATE,
            "", EMPTY_DATE, 0, false);
    }

    /** {@code Decimal.ToInt32(valor * 100)}: trunca. */
    static BigDecimal times100(BigDecimal value) {
        BigDecimal data = value == null ? BigDecimal.ZERO : value;
        return new BigDecimal(data.multiply(HUNDRED).toBigInteger());
    }

    static String key(int piece, int sizeRange) {
        return piece + "|" + sizeRange;
    }

    static Map<String, CustomsRowDTO> byKey(List<CustomsRowDTO> customs) {
        return customs.stream().collect(Collectors.toMap(row -> key(row.piece(), row.sizeRange()), Function.identity(),
            (first, second) -> second));
    }

    private static int value(Integer value) {
        return value == null ? 0 : value;
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }
}
