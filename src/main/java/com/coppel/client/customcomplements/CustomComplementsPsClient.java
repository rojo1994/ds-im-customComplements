package com.coppel.client.customcomplements;

import com.coppel.client.customcomplements.dto.AutomaticPermitDTO;
import com.coppel.client.customcomplements.dto.AutomaticPermitRequest;
import com.coppel.client.customcomplements.dto.BrandCreatedDTO;
import com.coppel.client.customcomplements.dto.BrandRequest;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.CauseChangeDTO;
import com.coppel.client.customcomplements.dto.CauseRequest;
import com.coppel.client.customcomplements.dto.CentralDataDTO;
import com.coppel.client.customcomplements.dto.CentralSaveRequest;
import com.coppel.client.customcomplements.dto.CodeGeneralDTO;
import com.coppel.client.customcomplements.dto.ExistsDTO;
import com.coppel.client.customcomplements.dto.ExpirationCheckRequest;
import com.coppel.client.customcomplements.dto.ExpirationMessageDTO;
import com.coppel.client.customcomplements.dto.FractionMaskDTO;
import com.coppel.client.customcomplements.dto.FractionMxnDTO;
import com.coppel.client.customcomplements.dto.IepsCategoryDTO;
import com.coppel.client.customcomplements.dto.IepsRequirementDTO;
import com.coppel.client.customcomplements.dto.IepsUnitDTO;
import com.coppel.client.customcomplements.dto.ImpiRequest;
import com.coppel.client.customcomplements.dto.InventoryRowDTO;
import com.coppel.client.customcomplements.dto.LockRequest;
import com.coppel.client.customcomplements.dto.LockStatusDTO;
import com.coppel.client.customcomplements.dto.NonTariffCauseDTO;
import com.coppel.client.customcomplements.dto.PermitColorRangeDTO;
import com.coppel.client.customcomplements.dto.PurchaseFractionDTO;
import com.coppel.client.customcomplements.dto.RegulationApplicabilityDTO;
import com.coppel.client.customcomplements.dto.SizeLimitsDTO;
import com.coppel.client.customcomplements.dto.SupplierDTO;
import com.coppel.client.customcomplements.dto.TariffsDTO;
import com.coppel.client.customcomplements.dto.WorkspaceCommitRequest;
import com.coppel.client.customcomplements.dto.WorkspaceSnapshotDTO;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.dto.ApiResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

/**
 * Cliente de ps-im-customComplements ({@code /v1/custom-complements}): un método por endpoint, con la respuesta
 * común { meta, data }. Los errores de ps llegan como excepciones de negocio con su mismo mensaje
 * ({@code PsClientErrorHandler}).
 */
@HttpExchange("/v1/custom-complements")
public interface CustomComplementsPsClient {

    // ------------------------------------------------------------------ Catálogos

    @GetExchange("/catalogs/importers")
    ApiResponseDTO<List<CatalogItemDTO>> importers();

    @GetExchange("/catalogs/countries")
    ApiResponseDTO<List<CatalogItemDTO>> countries();

    @GetExchange("/catalogs/weight-units")
    ApiResponseDTO<List<CatalogItemDTO>> weightUnits();

    @GetExchange("/catalogs/commercial-units")
    ApiResponseDTO<List<CatalogItemDTO>> commercialUnits();

    @GetExchange("/catalogs/quota-types")
    ApiResponseDTO<List<CatalogItemDTO>> quotaTypes();

    @GetExchange("/catalogs/producer-types")
    ApiResponseDTO<List<CatalogItemDTO>> producerTypes();

    @GetExchange("/catalogs/preferential-criteria")
    ApiResponseDTO<List<CatalogItemDTO>> preferentialCriteria();

    @GetExchange("/catalogs/fraction-mask")
    ApiResponseDTO<FractionMaskDTO> fractionMask();

    @GetExchange("/catalogs/customs-offices")
    ApiResponseDTO<List<CatalogItemDTO>> customsOffices();

    @GetExchange("/catalogs/exceptions")
    ApiResponseDTO<List<CatalogItemDTO>> exceptions(@RequestParam int regulation);

    @GetExchange("/catalogs/noms")
    ApiResponseDTO<List<CatalogItemDTO>> noms(@RequestParam String fraction, @RequestParam int regulation);

    @GetExchange("/catalogs/ieps-classes")
    ApiResponseDTO<List<CatalogItemDTO>> iepsClasses();

    @GetExchange("/catalogs/ieps-categories")
    ApiResponseDTO<List<IepsCategoryDTO>> iepsCategories(@RequestParam int classId);

    @GetExchange("/catalogs/ieps-units")
    ApiResponseDTO<List<IepsUnitDTO>> iepsUnits();

    @GetExchange("/catalogs/energy-units")
    ApiResponseDTO<List<CatalogItemDTO>> energyUnits();

    @GetExchange("/catalogs/brands")
    ApiResponseDTO<List<CatalogItemDTO>> brands(@RequestParam int supplierId, @RequestParam CustomsArea area);

    @GetExchange("/catalogs/brand-type")
    ApiResponseDTO<CatalogItemDTO> brandType(@RequestParam int brandId);

    @GetExchange("/catalogs/brand-types")
    ApiResponseDTO<List<CatalogItemDTO>> brandTypes();

    @PostExchange("/brands")
    ApiResponseDTO<BrandCreatedDTO> createBrand(@RequestBody BrandRequest request);

    @GetExchange("/catalogs/permit-color-ranges")
    ApiResponseDTO<List<PermitColorRangeDTO>> permitColorRanges();

    @GetExchange("/non-tariff-causes")
    ApiResponseDTO<List<NonTariffCauseDTO>> causes(@RequestParam boolean all);

    @PostExchange("/non-tariff-causes")
    ApiResponseDTO<CauseChangeDTO> createCause(@RequestBody CauseRequest request);

    @PutExchange("/non-tariff-causes/{causeId}")
    ApiResponseDTO<CauseChangeDTO> renameCause(@PathVariable long causeId, @RequestBody CauseRequest request);

    @PatchExchange("/non-tariff-causes/{causeId}/status")
    ApiResponseDTO<Void> toggleCause(@PathVariable long causeId);

    // ------------------------------------------------------------------ Código

    @GetExchange("/codes/{area}/{code}/exists")
    ApiResponseDTO<ExistsDTO> codeExists(@PathVariable CustomsArea area, @PathVariable int code);

    @GetExchange("/codes/{area}/{code}/supplier")
    ApiResponseDTO<SupplierDTO> supplier(@PathVariable CustomsArea area, @PathVariable int code);

    @GetExchange("/codes/{area}/{code}/inventory")
    ApiResponseDTO<List<InventoryRowDTO>> inventory(@PathVariable CustomsArea area, @PathVariable int code);

    @GetExchange("/codes/{area}/{code}/general")
    ApiResponseDTO<CodeGeneralDTO> general(@PathVariable CustomsArea area, @PathVariable int code);

    @GetExchange("/codes/{area}/{code}/purchase-fraction")
    ApiResponseDTO<PurchaseFractionDTO> purchaseFraction(@PathVariable CustomsArea area, @PathVariable int code);

    @GetExchange("/codes/{code}/size-limits")
    ApiResponseDTO<SizeLimitsDTO> sizeLimits(@PathVariable int code);

    @GetExchange("/codes/{area}/{code}/central-data")
    ApiResponseDTO<CentralDataDTO> centralData(@PathVariable CustomsArea area, @PathVariable int code);

    @PostExchange("/codes/{area}/{code}/impi/check")
    ApiResponseDTO<ExistsDTO> impiExists(@PathVariable CustomsArea area, @PathVariable int code,
                                         @RequestBody ImpiRequest request);

    @PostExchange("/codes/{area}/{code}/impi/delete")
    ApiResponseDTO<Void> deleteImpi(@PathVariable CustomsArea area, @PathVariable int code,
                                    @RequestBody ImpiRequest request);

    @PostExchange("/locks/check")
    ApiResponseDTO<LockStatusDTO> lockStatus(@RequestBody LockRequest request);

    @PostExchange("/locks/acquire")
    ApiResponseDTO<Void> acquireLock(@RequestBody LockRequest request);

    @PostExchange("/locks/release")
    ApiResponseDTO<Void> releaseLock(@RequestBody LockRequest request);

    // ------------------------------------------------------------------ Fracciones

    @GetExchange("/fractions/mxn")
    ApiResponseDTO<FractionMxnDTO> validateMxn(@RequestParam String fraction);

    @GetExchange("/fractions/usa")
    ApiResponseDTO<ExistsDTO> validateUsa(@RequestParam String fraction);

    @GetExchange("/fractions/regulations")
    ApiResponseDTO<RegulationApplicabilityDTO> regulationApplies(@RequestParam String fraction,
                                                                 @RequestParam(required = false) Integer regulation);

    @GetExchange("/fractions/ieps")
    ApiResponseDTO<IepsRequirementDTO> requiresIeps(@RequestParam String fraction);

    @GetExchange("/fractions/tariffs")
    ApiResponseDTO<TariffsDTO> tariffs(@RequestParam String fraction, @RequestParam int countryId);

    @PostExchange("/automatic-permit")
    ApiResponseDTO<AutomaticPermitDTO> automaticPermit(@RequestBody AutomaticPermitRequest request);

    @PostExchange("/expiration-check")
    ApiResponseDTO<ExpirationMessageDTO> checkExpiration(@RequestBody ExpirationCheckRequest request);

    // ------------------------------------------------------------------ Temporales y central

    @PostExchange("/workspaces/{area}/{code}/snapshot")
    ApiResponseDTO<WorkspaceSnapshotDTO> snapshot(@PathVariable CustomsArea area, @PathVariable int code);

    @PostExchange("/workspaces/{area}/{code}/commit")
    ApiResponseDTO<Void> commit(@PathVariable CustomsArea area, @PathVariable int code,
                                @RequestBody WorkspaceCommitRequest request);

    @PostExchange("/central/{area}/{code}")
    ApiResponseDTO<Void> saveCentral(@PathVariable CustomsArea area, @PathVariable int code,
                                     @RequestBody CentralSaveRequest request);
}
