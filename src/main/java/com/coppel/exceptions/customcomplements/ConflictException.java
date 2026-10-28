package com.coppel.exceptions.customcomplements;

import com.coppel.constants.customcomplements.ComplementsMeta;

/**
 * 409. {@link #COST_DIFFERENCE} va en {@code meta.status}: el front pregunta "¿Desea continuar?" y vuelve a mandar
 * Guardar confirmado (antes el MessageBox Sí / No de Guardar).
 */
public class ConflictException extends BusinessException {

    public static final String COST_DIFFERENCE = "COST_DIFFERENCE";

    public ConflictException(String message) {
        super(ComplementsMeta.META_CONFLICT, message);
    }

    public ConflictException(String status, String message) {
        super(ComplementsMeta.META_CONFLICT, status, message, null);
    }
}
