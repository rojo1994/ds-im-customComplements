package com.coppel.constants;

/**
 * AppMessages
 */
public class AppMessages {
    private AppMessages() {
        throw new IllegalStateException("No existe un constructor para la clase AppMessages");
    }

    public static final String CLIENT_ERROR = "CLIENT_ERROR";
    public static final String UNAUTHORISED_MESSAGE = "Usted no está autorizado para acceder este recurso.";
    public static final String INICIA_TRANSACCION = "Inicia Transaccion.";
    public static final String FINALIZA_TRANSACCION = "La Transaccion Finalizo con Exito.";
    public static final String FALLO_TRANSACCION = "Fallo Transaccion.";
}