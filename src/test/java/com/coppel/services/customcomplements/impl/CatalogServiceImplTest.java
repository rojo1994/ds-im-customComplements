package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.BrandCreatedDTO;
import com.coppel.client.customcomplements.dto.BrandRequest;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.SupplierDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.request.customcomplements.BrandInput;
import com.coppel.dto.response.customcomplements.BrandCreationDTO;
import com.coppel.exceptions.customcomplements.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Alta de marca: el proveedor sale del código y la marca se graba en el área de la sesión.
 */
class CatalogServiceImplTest {

    private final CustomComplementsPsClient psClient = mock(CustomComplementsPsClient.class);
    private CatalogServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CatalogServiceImpl(psClient);
        when(psClient.supplier(CustomsArea.ROPA, 412345)).thenReturn(ApiResponseDTO.ok(new SupplierDTO(77, "PROVEEDOR SA")));
    }

    @Test
    void createsTheBrandForTheCodeSupplierInItsArea() {
        when(psClient.createBrand(any())).thenReturn(ApiResponseDTO.ok(new BrandCreatedDTO(901, false)));
        when(psClient.brands(77, CustomsArea.ROPA)).thenReturn(ApiResponseDTO.ok(List.of(new CatalogItemDTO("901", "ACME"))));

        BrandCreationDTO result = service.createBrand(new BrandInput(CustomsArea.ROPA, "412345", 2, "  acme "));

        verify(psClient).createBrand(new BrandRequest(CustomsArea.ROPA, 77, 2, "ACME"));
        assertThat(result.brandId()).isEqualTo(901);
        assertThat(result.brands()).extracting(CatalogItemDTO::name).containsExactly("ACME");
    }

    @Test
    void duplicatedBrandIsAConflict() {
        when(psClient.createBrand(any())).thenReturn(ApiResponseDTO.ok(new BrandCreatedDTO(null, true)));

        assertThatThrownBy(() -> service.createBrand(new BrandInput(CustomsArea.ROPA, "412345", 2, "ACME")))
            .isInstanceOf(ConflictException.class)
            .hasMessage(AppMessages.MARCA_EXISTENTE);
        verify(psClient, never()).brands(anyInt(), any());
    }
}
