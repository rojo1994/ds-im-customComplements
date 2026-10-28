package com.coppel.controller.customcomplements;

import com.coppel.client.customcomplements.dto.TariffsDTO;
import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.request.customcomplements.AutomaticPermitCheckRequest;
import com.coppel.dto.request.customcomplements.CustomsInfoRequest;
import com.coppel.dto.request.customcomplements.FractionCheckRequest;
import com.coppel.dto.response.customcomplements.AutomaticPermitResultDTO;
import com.coppel.dto.response.customcomplements.CustomsInfoResultDTO;
import com.coppel.dto.response.customcomplements.FractionCheckDTO;
import com.coppel.services.customcomplements.CustomsInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/custom-complements/customs-info")
@RequiredArgsConstructor
@Tag(name = "Información aduanal", description = "Información aduanal de una pieza / rango (FrmAduanas)")
public class CustomsInfoController {

    private final CustomsInfoService customsInfoService;

    @Operation(summary = "Validar la fracción MXN",
        description = "Máscara, catálogo, reglas de la fracción (decreto, unidad de tarifa, azúcar, regulaciones, IEPS, "
            + "aviso sanitario), IMPI de la fracción anterior y aranceles")
    @PostMapping("/fraction")
    public ApiResponseDTO<FractionCheckDTO> checkFraction(@Valid @RequestBody FractionCheckRequest request) {
        return ApiResponseDTO.ok(customsInfoService.checkFraction(request));
    }

    @Operation(summary = "Aranceles para el país", description = "Antes ObtenerDatosAranceles")
    @GetMapping("/tariffs")
    public ApiResponseDTO<TariffsDTO> getTariffs(@RequestParam String fraction, @RequestParam int countryId) {
        return ApiResponseDTO.ok(customsInfoService.getTariffs(fraction, countryId));
    }

    @Operation(summary = "Validar la fracción USA")
    @GetMapping("/usa-fraction")
    public ApiResponseDTO<Void> validateUsaFraction(@RequestParam String fraction) {
        customsInfoService.validateUsaFraction(fraction);
        return ApiResponseDTO.ok(null);
    }

    @Operation(summary = "Permiso automático", description = "Antes ValidarPermisoAutomatico")
    @PostMapping("/automatic-permit")
    public ApiResponseDTO<AutomaticPermitResultDTO> automaticPermit(
        @Valid @RequestBody AutomaticPermitCheckRequest request) {
        return ApiResponseDTO.ok(customsInfoService.automaticPermit(request));
    }

    @Operation(summary = "Aceptar la información aduanal", description = "Antes [F5] Aceptar de FrmAduanas")
    @PostMapping("/accept")
    public ApiResponseDTO<CustomsInfoResultDTO> accept(@Valid @RequestBody CustomsInfoRequest request) {
        return ApiResponseDTO.ok(customsInfoService.accept(request));
    }
}
