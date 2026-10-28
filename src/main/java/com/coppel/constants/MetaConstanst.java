package com.coppel.constants;

import com.coppel.dto.MetaDTO;

/**
 * MetaConstanst
 */
public enum MetaConstanst
{
    META_OK("OK", 200, ""),
    META_FAIL_AUTH("FAIL AUTH", 401, "Usted no esta autorizado para acceder a este recurso"),
    META_ERROR("ERROR", 400, null);

    private final String status;
    private final int statusCode;
    private final String message;

    MetaConstanst(String status, int statusCode, String message) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * Metodo que regresa el objeto meta
     * @return Object - meta
     */
    public MetaDTO getMeta()
    {
        return MetaDTO.of(status, statusCode, message, null);
    }
}
