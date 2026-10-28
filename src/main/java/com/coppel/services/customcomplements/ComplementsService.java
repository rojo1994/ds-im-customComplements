package com.coppel.services.customcomplements;

import com.coppel.dto.request.customcomplements.SaveComplementsRequest;
import com.coppel.dto.response.customcomplements.SaveResultDTO;

/**
 * [F10] Guardar.
 */
public interface ComplementsService {

    SaveResultDTO save(SaveComplementsRequest request);
}
