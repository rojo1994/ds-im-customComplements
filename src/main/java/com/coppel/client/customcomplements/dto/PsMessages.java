package com.coppel.client.customcomplements.dto;

/**
 * Mensajes de validación de los records de ps-im-customComplements (las anotaciones se copian con el contrato; ds no
 * valida lo que manda, ps lo vuelve a validar).
 */
final class PsMessages {
    static final String AREA_REQUERIDA = "Favor de indicar el área.";
    static final String CODIGO_NO_VALIDO = "Favor de capturar el código a 6 dígitos.";
    static final String EMPLEADO_REQUERIDO = "No se recibió el número de empleado de la sesión.";
    static final String FRACCION_REQUERIDA = "Favor de capturar la fracción.";
    static final String CAUSA_REQUERIDA = "Favor de ingresar una causa para no aplicar Arancel";
    static final String DATOS_GENERALES_REQUERIDOS = "No se recibieron los datos generales del código.";
    static final String FECHA_REQUERIDA = "Favor de capturar la fecha.";

    private PsMessages() {
    }
}
