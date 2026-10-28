package com.coppel.exceptions.customcomplements;

import com.coppel.constants.customcomplements.ComplementsMeta;
import lombok.Getter;

/**
 * Excepción base de negocio: lleva el meta HTTP con el que debe responderse y, opcionalmente, un estado propio
 * ({@code meta.status}) para que el front distinga la confirmación que debe pedir.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final transient ComplementsMeta meta;
    private final String status;

    public BusinessException(ComplementsMeta meta, String message) {
        this(meta, null, message, null);
    }

    public BusinessException(ComplementsMeta meta, String message, Throwable cause) {
        this(meta, null, message, cause);
    }

    public BusinessException(ComplementsMeta meta, String status, String message, Throwable cause) {
        super(message, cause);
        this.meta = meta;
        this.status = status;
    }
}
