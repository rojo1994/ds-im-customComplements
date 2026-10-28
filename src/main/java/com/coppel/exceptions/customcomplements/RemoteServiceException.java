package com.coppel.exceptions.customcomplements;

import com.coppel.constants.customcomplements.ComplementsMeta;

/** 502: ps-im-customComplements no respondió o respondió con un error que no es de negocio. */
public class RemoteServiceException extends BusinessException {
    public RemoteServiceException(String message, Throwable cause) {
        super(ComplementsMeta.META_ERROR_REMOTO, message, cause);
    }
}
