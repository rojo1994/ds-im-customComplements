package com.coppel.controller.customcomplements;

import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.request.customcomplements.BrandChangeRequest;
import com.coppel.dto.request.customcomplements.SaveComplementsRequest;
import com.coppel.dto.request.customcomplements.SessionRequest;
import com.coppel.dto.response.customcomplements.BrandChangeDTO;
import com.coppel.dto.response.customcomplements.CodeSessionDTO;
import com.coppel.dto.response.customcomplements.SaveResultDTO;
import com.coppel.services.customcomplements.ComplementsService;
import com.coppel.services.customcomplements.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/custom-complements")
@RequiredArgsConstructor
@Tag(name = "Complementos aduana", description = "Abrir, guardar y soltar un código (FrmComplementosAduanales)")
public class SessionController {

    private final SessionService sessionService;
    private final ComplementsService complementsService;

    @Operation(summary = "Abrir un código",
        description = "Antes CargarInformacionCodigo: valida el código, revisa que nadie lo tenga, carga proveedor, marcas, "
            + "inventario, datos guardados y temporales y toma el código")
    @PostMapping("/sessions/open")
    public ApiResponseDTO<CodeSessionDTO> open(@Valid @RequestBody SessionRequest request) {
        return ApiResponseDTO.ok(sessionService.open(request));
    }

    @Operation(summary = "Soltar el código", description = "Antes [F12] Limpiar / Salir (VerificarCodigoEnUso 3)")
    @PostMapping("/sessions/close")
    public ApiResponseDTO<Void> close(@Valid @RequestBody SessionRequest request) {
        sessionService.close(request);
        return ApiResponseDTO.ok(null);
    }

    @Operation(summary = "Cambio de marca", description = "Antes ValidaImpi + ObtenerTipoMarca")
    @PostMapping("/brands/change")
    public ApiResponseDTO<BrandChangeDTO> changeBrand(@Valid @RequestBody BrandChangeRequest request) {
        return ApiResponseDTO.ok(sessionService.changeBrand(request));
    }

    @Operation(summary = "Guardar",
        description = "Antes [F10] Guardar. Si la suma de costos no coincide responde 409 con meta.status = COST_DIFFERENCE; "
            + "se vuelve a mandar con confirmCostDifference = true")
    @PostMapping("/complements/save")
    public ApiResponseDTO<SaveResultDTO> save(@Valid @RequestBody SaveComplementsRequest request) {
        return ApiResponseDTO.ok(complementsService.save(request));
    }
}
