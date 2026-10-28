package com.coppel.controller.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.IepsCategoryDTO;
import com.coppel.client.customcomplements.dto.NonTariffCauseDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.request.customcomplements.BrandInput;
import com.coppel.dto.request.customcomplements.CauseInput;
import com.coppel.dto.response.customcomplements.BrandCreationDTO;
import com.coppel.dto.response.customcomplements.ScreenCatalogsDTO;
import com.coppel.services.customcomplements.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/v1/custom-complements")
@RequiredArgsConstructor
@Tag(name = "Catálogos", description = "Combos de la pantalla y causas para no aplicar arancel preferencial")
public class CatalogController {

    private final CatalogService catalogService;

    @Operation(summary = "Combos de la pantalla y de los subformularios",
        description = "Importadoras (sin importadoras la opción no abre), países, unidades, cuotas, productor, criterio, "
            + "máscaras de fracción, aduanas, excepciones de aviso sanitario, IEPS, consumo energético, colores del % "
            + "de permiso automático, causas activas y tipos de marca")
    @GetMapping("/catalogs")
    public ApiResponseDTO<ScreenCatalogsDTO> getCatalogs() {
        return ApiResponseDTO.ok(catalogService.getScreenCatalogs());
    }

    @Operation(summary = "Categorías IEPS de una clase", description = "Antes cbClases_SelectionChangeCommitted")
    @GetMapping("/catalogs/ieps-categories")
    public ApiResponseDTO<List<IepsCategoryDTO>> getIepsCategories(
        @RequestParam @Positive(message = AppMessages.SELECCIONAR_CLASE) int classId) {
        return ApiResponseDTO.ok(catalogService.getIepsCategories(classId));
    }

    @Operation(summary = "Excepciones de una regulación")
    @GetMapping("/catalogs/exceptions")
    public ApiResponseDTO<List<CatalogItemDTO>> getExceptions(@RequestParam @Min(1) @Max(5) int regulation) {
        return ApiResponseDTO.ok(catalogService.getExceptions(regulation));
    }

    @Operation(summary = "NOM de la fracción (4 cumplimiento, 5 información comercial)")
    @GetMapping("/catalogs/noms")
    public ApiResponseDTO<List<CatalogItemDTO>> getNoms(@RequestParam @NotBlank String fraction,
                                                        @RequestParam @Min(4) @Max(5) int regulation) {
        return ApiResponseDTO.ok(catalogService.getNoms(fraction.trim(), regulation));
    }

    @Operation(summary = "Alta de marca",
        description = "Para el proveedor del código en su área; 409 si ya existe. Regresa el combo de marcas actualizado")
    @PostMapping("/brands")
    public ApiResponseDTO<BrandCreationDTO> createBrand(@Valid @RequestBody BrandInput input) {
        return ApiResponseDTO.ok(catalogService.createBrand(input));
    }

    @Operation(summary = "Causas para no aplicar arancel preferencial", description = "all=true: también las inactivas")
    @GetMapping("/non-tariff-causes")
    public ApiResponseDTO<List<NonTariffCauseDTO>> getCauses(@RequestParam(defaultValue = "false") boolean all) {
        return ApiResponseDTO.ok(catalogService.getCauses(all));
    }

    @Operation(summary = "Alta de causa", description = "Antes [F5] Agregar de FrmCausasNoAplicarAP")
    @PostMapping("/non-tariff-causes")
    public ApiResponseDTO<Void> createCause(@Valid @RequestBody CauseInput input) {
        catalogService.createCause(input);
        return ApiResponseDTO.ok(null);
    }

    @Operation(summary = "Cambio de descripción", description = "Antes doble clic en la causa + [F5]")
    @PutMapping("/non-tariff-causes/{causeId}")
    public ApiResponseDTO<Void> renameCause(@PathVariable @Positive long causeId, @Valid @RequestBody CauseInput input) {
        catalogService.renameCause(causeId, input);
        return ApiResponseDTO.ok(null);
    }

    @Operation(summary = "Baja / alta de causa", description = "Antes [F10] Grabar con la casilla Baja")
    @PatchMapping("/non-tariff-causes/{causeId}/status")
    public ApiResponseDTO<Void> toggleCause(@PathVariable @Positive long causeId) {
        catalogService.toggleCause(causeId);
        return ApiResponseDTO.ok(null);
    }
}
