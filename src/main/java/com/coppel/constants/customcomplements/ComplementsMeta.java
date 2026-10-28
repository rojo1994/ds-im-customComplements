package com.coppel.constants.customcomplements;

import com.coppel.dto.MetaDTO;

/**
 * Estados con los que responde el módulo (MetaConstanst común sólo trae OK, 401 y 400).
 */
public enum ComplementsMeta {
    META_OK("OK", 200),
    META_ERROR("ERROR", 400),
    META_NOT_FOUND("NOT FOUND", 404),
    META_CONFLICT("CONFLICT", 409),
    META_ERROR_REMOTO("BAD GATEWAY", 502),
    META_ERROR_INTERNO("INTERNAL SERVER ERROR", 500);

    private final String status;
    private final int statusCode;

    ComplementsMeta(String status, int statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    /** Meta de la respuesta con el mensaje para el usuario y, si se expone, el detalle técnico. */
    public MetaDTO getMeta(String message, String devMessage) {
        return MetaDTO.of(status, statusCode, message, devMessage);
    }

    /**
     * Meta con un estado propio en lugar del genérico (por ejemplo {@code COST_DIFFERENCE}) para que el front sepa
     * qué confirmación pedir.
     */
    public MetaDTO getMeta(String customStatus, String message, String devMessage) {
        return MetaDTO.of(customStatus == null ? status : customStatus, statusCode, message, devMessage);
    }
}
