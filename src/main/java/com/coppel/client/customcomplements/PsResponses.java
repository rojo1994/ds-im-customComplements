package com.coppel.client.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.ApiResponseDTO;
import com.coppel.exceptions.customcomplements.RemoteServiceException;

import java.util.List;

/**
 * Lectura del {@code data} de las respuestas de ps.
 */
public final class PsResponses {

    private PsResponses() {
    }

    /** {@code data} de la respuesta; ps siempre lo manda en los endpoints que regresan algo. */
    public static <T> T data(ApiResponseDTO<T> response) {
        if (response == null || response.data() == null) {
            throw new RemoteServiceException(AppMessages.ERROR_PERSISTENCIA,
                new IllegalStateException("ps respondió sin data"));
        }
        return response.data();
    }

    /** Lista vacía si ps no mandó data. */
    public static <T> List<T> list(ApiResponseDTO<List<T>> response) {
        return response == null || response.data() == null ? List.of() : response.data();
    }
}
