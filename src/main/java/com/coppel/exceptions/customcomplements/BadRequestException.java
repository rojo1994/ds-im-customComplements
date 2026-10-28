package com.coppel.exceptions.customcomplements;

import com.coppel.constants.customcomplements.ComplementsMeta;

/** Validación de negocio: el mensaje es el MessageBox del escritorio. */
public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(ComplementsMeta.META_ERROR, message);
    }
}
