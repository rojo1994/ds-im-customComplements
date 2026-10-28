package com.coppel.controller.customcomplements;

import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;
import com.coppel.dto.request.customcomplements.ExpirationRequest;
import com.coppel.dto.request.customcomplements.IepsRequest;
import com.coppel.dto.request.customcomplements.RegulationsRequest;
import com.coppel.dto.request.customcomplements.SanitaryNoticeRequest;
import com.coppel.dto.request.customcomplements.SizeRangesRequest;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import com.coppel.dto.response.customcomplements.RegulationOptionsDTO;
import com.coppel.dto.response.customcomplements.RegulationsResultDTO;
import com.coppel.dto.response.customcomplements.SanitaryNoticeResultDTO;
import com.coppel.dto.response.customcomplements.SizeRangesResultDTO;
import com.coppel.services.customcomplements.DetailsService;
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
@RequestMapping("/v1/custom-complements")
@RequiredArgsConstructor
@Tag(name = "Subformularios", description = "Regulaciones, aviso sanitario, especificaciones técnicas, IEPS y rangos de tallas")
public class DetailsController {

    private final DetailsService detailsService;

    @Operation(summary = "Secciones de regulaciones de la fracción", description = "Antes FrmRegulaciones_Load")
    @GetMapping("/regulations/options")
    public ApiResponseDTO<RegulationOptionsDTO> getRegulationOptions(@RequestParam String fraction) {
        return ApiResponseDTO.ok(detailsService.getRegulationOptions(fraction));
    }

    @Operation(summary = "Aceptar regulaciones", description = "Antes ValidarControles + LlenarTemporales")
    @PostMapping("/regulations/accept")
    public ApiResponseDTO<RegulationsResultDTO> acceptRegulations(@Valid @RequestBody RegulationsRequest request) {
        return ApiResponseDTO.ok(detailsService.acceptRegulations(request));
    }

    @Operation(summary = "Aceptar aviso sanitario", description = "Antes FrmAvisoSanitario.Guardar + MostrarMensajeAvisoSanitario")
    @PostMapping("/sanitary-notice/accept")
    public ApiResponseDTO<SanitaryNoticeResultDTO> acceptSanitaryNotice(@Valid @RequestBody SanitaryNoticeRequest request) {
        return ApiResponseDTO.ok(detailsService.acceptSanitaryNotice(request));
    }

    @Operation(summary = "Aceptar especificaciones técnicas", description = "Antes [F5] Aceptar de FrmEspecificaciones")
    @PostMapping("/specifications/accept")
    public ApiResponseDTO<SpecificationRowDTO> acceptSpecifications(@Valid @RequestBody SpecificationRowDTO request) {
        return ApiResponseDTO.ok(detailsService.acceptSpecifications(request));
    }

    @Operation(summary = "Aceptar IEPS", description = "Antes [F5] Aceptar de FrmIEPS")
    @PostMapping("/ieps/accept")
    public ApiResponseDTO<IepsRowDTO> acceptIeps(@Valid @RequestBody IepsRequest request) {
        return ApiResponseDTO.ok(detailsService.acceptIeps(request));
    }

    @Operation(summary = "Aceptar rangos de tallas", description = "Antes FrmRangoDeTallas + RangoTallas")
    @PostMapping("/size-ranges/accept")
    public ApiResponseDTO<SizeRangesResultDTO> acceptSizeRanges(@Valid @RequestBody SizeRangesRequest request) {
        return ApiResponseDTO.ok(detailsService.acceptSizeRanges(request));
    }

    @Operation(summary = "Vigencia de una fecha", description = "dbo.fun_validarfechavencimiento (color de la fecha)")
    @PostMapping("/expiration/check")
    public ApiResponseDTO<ExpirationStatusDTO> checkExpiration(@Valid @RequestBody ExpirationRequest request) {
        return ApiResponseDTO.ok(detailsService.checkExpiration(request));
    }
}
