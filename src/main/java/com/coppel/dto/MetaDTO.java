package com.coppel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Meta común de las respuestas { meta, data }. Mismos campos que el MetaDTO de ps-im-CustomsDocumentsM3, como
 * record: el timestamp y el transactionID se generan al crearlo.
 */
@JsonInclude(Include.NON_DEFAULT)
public record MetaDTO(
    String status,
    int statusCode,
    String timestamp,
    String transactionID,
    String devMessage,
    String message,
    boolean rollback
) {
    public static MetaDTO of(String status, int statusCode, String message, String devMessage) {
        return new MetaDTO(status, statusCode, LocalDateTime.now().toString(), UUID.randomUUID().toString(),
            devMessage, message, false);
    }
}
