package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.CodeGeneralDTO;
import com.coppel.client.customcomplements.dto.ImpiRequest;
import com.coppel.client.customcomplements.dto.InventoryRowDTO;
import com.coppel.client.customcomplements.dto.LockRequest;
import com.coppel.client.customcomplements.dto.SupplierDTO;
import com.coppel.client.customcomplements.dto.WorkspaceSnapshotDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.constants.customcomplements.ExpirationDocument;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.request.customcomplements.BrandChangeRequest;
import com.coppel.dto.request.customcomplements.SessionRequest;
import com.coppel.dto.response.customcomplements.BrandChangeDTO;
import com.coppel.dto.response.customcomplements.CodeHeaderDTO;
import com.coppel.dto.response.customcomplements.CodeSessionDTO;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import com.coppel.dto.response.customcomplements.FractionRulesDTO;
import com.coppel.dto.response.customcomplements.InventoryDTO;
import com.coppel.exceptions.customcomplements.ConflictException;
import com.coppel.exceptions.customcomplements.NotFoundException;
import com.coppel.services.customcomplements.SessionService;
import com.coppel.services.customcomplements.impl.SessionRules.Components;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coppel.client.customcomplements.PsResponses.data;
import static com.coppel.client.customcomplements.PsResponses.list;

/**
 * Carga del código con el mismo orden y los mismos mensajes que CargarInformacionCodigo: validar → ¿en uso? →
 * proveedor → marcas → inventario → datos del código → tipo de marca → temporales (snapshot) → tomar el código.
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final CustomComplementsPsClient psClient;
    private final FractionRulesResolver fractionRules;
    private final ExpirationChecker expirationChecker;

    @Override
    public CodeSessionDTO open(SessionRequest request) {
        CustomsArea area = request.area();
        int code = request.codeNumber();
        LockRequest lock = new LockRequest(area, code, request.employee());

        if (!data(psClient.codeExists(area, code)).exists()) {
            throw new NotFoundException(AppMessages.CODIGO_NO_REGISTRADO);
        }
        if (data(psClient.lockStatus(lock)).inUse()) {
            throw new ConflictException(AppMessages.CODIGO_EN_USO);
        }
        SupplierDTO supplier = supplier(area, code);
        List<CatalogItemDTO> brands = list(psClient.brands(supplier.id(), area));
        if (brands.isEmpty()) {
            throw new NotFoundException(NotFoundException.NO_BRANDS, AppMessages.SIN_MARCAS);
        }
        List<InventoryRowDTO> inventoryRows = list(psClient.inventory(area, code));
        if (inventoryRows.isEmpty()) {
            throw new NotFoundException(AppMessages.SIN_INVENTARIO);
        }
        CodeGeneralDTO general = data(psClient.general(area, code));

        // Tipo de marca de la marca del combo (si no tiene, el combo quedaba sin selección).
        Optional<Integer> brandId = SessionRules.defaultBrand(general, brands);
        CatalogItemDTO brandType = brandId.map(id -> data(psClient.brandType(id))).orElse(new CatalogItemDTO("", ""));
        Integer selectedBrand = brandType.id().isEmpty() ? null : brandId.orElse(null);

        List<String> warnings = new ArrayList<>();
        String articleSizes = "";
        if (area.isClothing()) {
            articleSizes = SessionRules.articleSizes(data(psClient.sizeLimits(code)));
            if (articleSizes.isEmpty()) {
                warnings.add(AppMessages.SIN_TALLAS);
            }
        }

        WorkspaceSnapshotDTO snapshot = data(psClient.snapshot(area, code));
        Map<String, FractionRulesDTO> rules = new LinkedHashMap<>();
        snapshot.customs().forEach(row -> rules.computeIfAbsent(row.fraction(), fractionRules::resolve));
        List<CustomsRowDTO> customs = snapshot.customs().stream()
            .map(row -> withTariffUnit(row, rules.get(row.fraction())))
            .toList();

        Components components = SessionRules.components(area, customs, snapshot.sizes(),
            general.representativePiece(), general.representativeRange(), articleSizes);
        int count = SessionRules.componentCount(area, components.rows());
        boolean combined = count > 1;

        ExpirationStatusDTO brandImageStatus = general.brandImage()
            ? expirationChecker.check(general.brandImageExpiration(), ExpirationDocument.BRAND_IMAGE)
            : ExpirationStatusDTO.valid();
        if (brandImageStatus.status() != ExpirationStatus.VALID) {
            warnings.add(brandImageStatus.message());
        }
        boolean requiresSanitaryNotice = customs.stream()
            .anyMatch(row -> rules.get(row.fraction()) != null && rules.get(row.fraction()).sanitaryNotice());

        List<RegulationRowDTO> sanitaryNotice = snapshot.regulations().stream()
            .filter(row -> row.regulation() == FractionRulesResolver.SANITARY_NOTICE)
            .toList();
        List<RegulationRowDTO> regulations = snapshot.regulations().stream()
            .filter(row -> row.regulation() != FractionRulesResolver.SANITARY_NOTICE)
            .toList();

        CodeHeaderDTO header = new CodeHeaderDTO(area, code, supplier.id(), supplier.name(), general.importerId(), brands,
            selectedBrand, parse(brandType.id()), brandType.name(), general.model(), general.purchaseBrand(),
            general.purchaseModel(), general.ftz(), general.pendingLabel(), general.brandImage(),
            general.brandImage() ? general.brandImageExpiration() : null, general.sanitaryNoticeInProcess(), combined,
            combined ? count : null, combined ? general.generalDescription() : "",
            SessionRules.generalCost(area, general.interiorCost()), components.representative().piece(),
            components.representative().range(), articleSizes, general.registered(),
            area.isClothing() ? null : data(psClient.purchaseFraction(area, code)).fraction());
        List<InventoryDTO> inventory = inventoryRows.stream().map(row -> SessionRules.inventory(area, row)).toList();

        // Antes VerificarCodigoEnUso(2) al terminar de cargar.
        psClient.acquireLock(lock);
        return new CodeSessionDTO(header, inventory, components.rows(), customs, rules, snapshot.sizes(), regulations,
            sanitaryNotice, snapshot.specifications(), snapshot.ieps(), snapshot.defaultCountry(), brandImageStatus,
            requiresSanitaryNotice, warnings);
    }

    /** Antes Inicializar / FormClosing: VerificarCodigoEnUso(3); las temporales ya no existen. */
    @Override
    public void close(SessionRequest request) {
        psClient.releaseLock(new LockRequest(request.area(), request.codeNumber(), request.employee()));
    }

    /** Antes ValidaImpi + ObtenerTipoMarca (cbMarca_SelectionChangeCommitted). */
    @Override
    public BrandChangeDTO changeBrand(BrandChangeRequest request) {
        boolean impiExists = request.previousBrandId() != null && request.previousBrandId() > 0
            && !request.previousBrandId().equals(request.brandId())
            && data(psClient.impiExists(request.area(), request.code(),
                new ImpiRequest(String.valueOf(request.previousBrandId()), 0, 0, ""))).exists();
        CatalogItemDTO type = data(psClient.brandType(request.brandId()));
        return new BrandChangeDTO(parse(type.id()), type.name(), impiExists);
    }

    private SupplierDTO supplier(CustomsArea area, int code) {
        try {
            SupplierDTO supplier = data(psClient.supplier(area, code));
            if (supplier.name() == null || supplier.name().isBlank()) {
                throw new NotFoundException(AppMessages.SIN_PROVEEDOR);
            }
            return supplier;
        } catch (NotFoundException ex) {
            throw new NotFoundException(AppMessages.SIN_PROVEEDOR);
        }
    }

    /** obtenerdatosaduanas no regresa la unidad de tarifa: el escritorio la tomaba de validarfraccionmxn. */
    private static CustomsRowDTO withTariffUnit(CustomsRowDTO row, FractionRulesDTO rules) {
        int tariffUnit = rules == null ? 0 : rules.tariffUnitId();
        return new CustomsRowDTO(row.piece(), row.sizeRange(), row.fraction(), row.appliesDecree(),
            row.customsDescription(), row.composition(), row.complement(), row.englishDescription(), row.countryId(),
            row.countryName(), row.usaFraction(), row.unitCost(), row.permitCost(), row.requiresPermit(),
            row.permitPercentage(), row.weightUnitId(), row.weight(),
            CustomsRules.weightInKg(row.weightUnitId(), row.weight()), tariffUnit, row.commercialUnitId(),
            row.componentPieces(), row.tariffValue(), row.sugarKg(), row.compensatoryQuota(), row.quotaTypeId(),
            row.referenceCost(), row.specificReferenceCost(), row.quotaToPay(), row.quotaAmount(),
            row.preferentialTariff(), row.nonTariffCauseId(), row.tmec(), row.producer(), row.producerTypeId(),
            row.preferentialCriterionId(), row.normalFrontierTariff(), row.normalInteriorTariff(),
            row.preferentialFrontierTariff(), row.preferentialInteriorTariff(), row.applyFrontierTariff(),
            row.applyInteriorTariff(), row.color(), row.sanitaryNotice());
    }

    private static int parse(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
