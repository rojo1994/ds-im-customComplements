package com.coppel.dto.customcomplements;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Renglón de {@code tmp_regulaciones<área><código>}: 1 aviso sanitario (por aduana), 2 certificado fitosanitario,
 * 3 inspección PROFEPA, 4 NOM de cumplimiento, 5 NOM de información comercial. Se lee con
 * {@code proc_mxtrf010obtenerdatosregulaciones} y se escribe con {@code proc_mxtrf010actualizartmpregulaciones}.
 *
 * @param nomOrOffice {@code idu_nomoaduana}: la aduana en la regulación 1 y la NOM en la 4 / 5
 * @param validity    {@code fec_vigenciaregulacion}; 1900-01-01 cuando no aplica, como el escritorio
 */
public record RegulationRowDTO(
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    @NotNull @Size(max = 22) String fraction,
    @Min(1) @Max(5) int regulation,
    int nomOrOffice,
    @NotNull @Size(max = 25) String certificate,
    @NotNull LocalDate validity,
    int exceptionId,
    boolean nomInProcess
) {
}
