package com.coppel.exceptions.customcomplements;

import com.coppel.constants.customcomplements.ComplementsMeta;

/**
 * 404. {@link #NO_BRANDS} va en {@code meta.status}: el proveedor del código no tiene marcas y el front ofrece el alta
 * de marca antes de volver a abrir el código.
 */
public class NotFoundException extends BusinessException {

    public static final String NO_BRANDS = "NO_BRANDS";

    public NotFoundException(String message) {
        super(ComplementsMeta.META_NOT_FOUND, message);
    }

    public NotFoundException(String status, String message) {
        super(ComplementsMeta.META_NOT_FOUND, status, message, null);
    }
}
