package com.coppel.services.customcomplements;

import com.coppel.dto.request.customcomplements.BrandChangeRequest;
import com.coppel.dto.request.customcomplements.SessionRequest;
import com.coppel.dto.response.customcomplements.BrandChangeDTO;
import com.coppel.dto.response.customcomplements.CodeSessionDTO;

/**
 * Sesión de trabajo sobre un código (antes CargarInformacionCodigo / Inicializar / FormClosing).
 */
public interface SessionService {

    CodeSessionDTO open(SessionRequest request);

    void close(SessionRequest request);

    BrandChangeDTO changeBrand(BrandChangeRequest request);
}
