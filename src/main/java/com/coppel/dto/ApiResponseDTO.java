package com.coppel.dto;

import com.coppel.constants.MetaConstanst;

/**
 * Respuesta común { meta, data }.
 */
public record ApiResponseDTO<T>(
    MetaDTO meta,
    T data
) {
    public static <T> ApiResponseDTO<T> ok(T data) {
        return new ApiResponseDTO<>(MetaConstanst.META_OK.getMeta(), data);
    }

    public static ApiResponseDTO<Void> error(MetaDTO meta) {
        return new ApiResponseDTO<>(meta, null);
    }
}
