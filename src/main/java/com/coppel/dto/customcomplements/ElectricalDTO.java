package com.coppel.dto.customcomplements;

/**
 * Renglón de {@code tmp_especificacionesdetalle<área><código>}: datos eléctricos del producto (componente 1), del
 * adaptador de entrada / salida (componente 2) o del control remoto (componente 3). Los campos que no aplican al
 * componente viajan vacíos, como en el escritorio ({@code VARCHAR(13)}).
 */
public record ElectricalDTO(
    String volts,
    String watts,
    String hertz,
    String amperes,
    String megaAmperes,
    String directCurrent,
    String batteries,
    String batteryType,
    String batteryVoltage
) {
    public static final ElectricalDTO EMPTY = new ElectricalDTO("", "", "", "", "", "", "", "", "");
}
