package com.coppel.dto.customcomplements;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Especificaciones técnicas de una pieza / rango: {@code tmp_especificaciones<área><código>} más sus renglones de
 * {@code tmp_especificacionesdetalle}. Se leen con {@code proc_mxtrf010obtenerdatosespecificaciones} y se escriben
 * con {@code proc_mxtrf010actualizartmpespecificaciones}. Las cantidades de consumo son texto con coma decimal
 * ({@code '0,0000'}), igual que las columnas.
 */
public record SpecificationRowDTO(
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    boolean hasComponents,
    boolean under24Volts,
    boolean hasAdapter,
    boolean hasRemoteControl,
    boolean hasEnergyConsumption,
    int consumptionUnitId,
    @NotNull @Size(max = 12) String consumptionQuantity,
    int standbyUnitId,
    @NotNull @Size(max = 12) String standbyQuantity,
    int consumedUnitId,
    @NotNull @Size(max = 12) String consumedQuantity,
    @NotNull ElectricalDTO product,
    @NotNull ElectricalDTO adapterInput,
    @NotNull ElectricalDTO adapterOutput,
    @NotNull ElectricalDTO remoteControl
) {
}
