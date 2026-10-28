package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.ExpirationCheckRequest;
import com.coppel.constants.customcomplements.ExpirationDocument;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.coppel.client.customcomplements.PsResponses.data;

/**
 * dbo.fun_validarfechavencimiento con la interpretación del escritorio: mensaje con "vencer" → naranja, cualquier otro
 * mensaje → rojo, sin mensaje → vigente.
 */
@Component
@RequiredArgsConstructor
public class ExpirationChecker {

    /** Fecha "vacía" del escritorio: no se valida. */
    static final LocalDate EMPTY_DATE = LocalDate.of(1900, 1, 1);

    private final CustomComplementsPsClient psClient;

    public ExpirationStatusDTO check(LocalDate date, ExpirationDocument document) {
        if (date == null || !date.isAfter(EMPTY_DATE)) {
            return ExpirationStatusDTO.valid();
        }
        String message = data(psClient.checkExpiration(new ExpirationCheckRequest(date, document.id()))).message();
        return status(message);
    }

    static ExpirationStatusDTO status(String message) {
        if (message == null || message.isBlank()) {
            return ExpirationStatusDTO.valid();
        }
        return new ExpirationStatusDTO(message.contains("vencer") ? ExpirationStatus.EXPIRING : ExpirationStatus.EXPIRED,
            message);
    }
}
