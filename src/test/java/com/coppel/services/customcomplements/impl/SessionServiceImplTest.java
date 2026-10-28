package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.CodeGeneralDTO;
import com.coppel.client.customcomplements.dto.ExistsDTO;
import com.coppel.client.customcomplements.dto.FractionMxnDTO;
import com.coppel.client.customcomplements.dto.IepsRequirementDTO;
import com.coppel.client.customcomplements.dto.InventoryRowDTO;
import com.coppel.client.customcomplements.dto.LockRequest;
import com.coppel.client.customcomplements.dto.LockStatusDTO;
import com.coppel.client.customcomplements.dto.PurchaseFractionDTO;
import com.coppel.client.customcomplements.dto.RegulationApplicabilityDTO;
import com.coppel.client.customcomplements.dto.SizeLimitsDTO;
import com.coppel.client.customcomplements.dto.SupplierDTO;
import com.coppel.client.customcomplements.dto.WorkspaceSnapshotDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.request.customcomplements.SessionRequest;
import com.coppel.dto.response.customcomplements.CodeSessionDTO;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import com.coppel.exceptions.customcomplements.ConflictException;
import com.coppel.exceptions.customcomplements.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Carga del código en el orden de CargarInformacionCodigo.
 */
class SessionServiceImplTest {

    private static final int CODE = 412345;

    private final CustomComplementsPsClient psClient = mock(CustomComplementsPsClient.class);
    private SessionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SessionServiceImpl(psClient, new FractionRulesResolver(psClient), new ExpirationChecker(psClient));
        when(psClient.codeExists(any(), anyInt())).thenReturn(ok(new ExistsDTO(true)));
        when(psClient.lockStatus(any())).thenReturn(ok(new LockStatusDTO(false)));
        when(psClient.supplier(any(), anyInt())).thenReturn(ok(new SupplierDTO(77, "PROVEEDOR SA")));
        when(psClient.brands(anyInt(), any())).thenReturn(ok(List.of(new CatalogItemDTO("5", "OTRA"),
            new CatalogItemDTO("9", "MARCA COMPRAS"))));
        when(psClient.inventory(any(), anyInt())).thenReturn(ok(List.of(new InventoryRowDTO("TIJ", 4,
            new BigDecimal("1000")))));
        when(psClient.brandType(anyInt())).thenReturn(ok(new CatalogItemDTO("2", "PROPIA")));
        when(psClient.sizeLimits(anyInt())).thenReturn(ok(new SizeLimitsDTO(22, 26)));
        when(psClient.validateMxn(anyString())).thenReturn(ok(new FractionMxnDTO(true, false, 6, "PIEZA", false)));
        when(psClient.regulationApplies(anyString(), any())).thenReturn(ok(new RegulationApplicabilityDTO(false)));
        when(psClient.requiresIeps(anyString())).thenReturn(ok(new IepsRequirementDTO(false)));
    }

    @Test
    void newClothingCodeStartsWithPieceOneAndAllSizes() {
        when(psClient.general(any(), anyInt())).thenReturn(ok(new CodeGeneralDTO(false, 3, null, "MARCA COMPRAS", "",
            "MOD-1", "MOD-1", true, false, LocalDate.of(1900, 1, 1), false, false, false, "", 0, 0,
            new BigDecimal("15.75"))));
        when(psClient.snapshot(any(), anyInt())).thenReturn(ok(new WorkspaceSnapshotDTO(List.of(),
            new CatalogItemDTO("12", "CHINA"), List.of(new SizeRowDTO(1, 22, 1), new SizeRowDTO(1, 26, 1)), List.of(),
            List.of(), List.of())));

        CodeSessionDTO session = service.open(new SessionRequest(CustomsArea.ROPA, "412345", 90001));

        assertThat(session.components()).containsExactly(new ComponentDTO(1, 1, "22-26", true));
        assertThat(session.header().brandId()).isEqualTo(9);
        assertThat(session.header().generalCost()).isEqualByComparingTo("15.75");
        assertThat(session.header().combined()).isFalse();
        assertThat(session.inventory().getFirst().cost()).isEqualByComparingTo("250.00");
        verify(psClient).acquireLock(new LockRequest(CustomsArea.ROPA, CODE, 90001));
    }

    @Test
    void furnitureHeaderCarriesThePurchaseFraction() {
        when(psClient.purchaseFraction(any(), anyInt())).thenReturn(ok(new PurchaseFractionDTO("9403.60.99")));
        when(psClient.general(any(), anyInt())).thenReturn(ok(new CodeGeneralDTO(false, 3, null, "MARCA COMPRAS", "",
            "MOD-1", "MOD-1", true, false, LocalDate.of(1900, 1, 1), false, false, false, "", 0, 0,
            new BigDecimal("1575"))));
        when(psClient.snapshot(any(), anyInt())).thenReturn(ok(new WorkspaceSnapshotDTO(List.of(),
            new CatalogItemDTO("12", "CHINA"), List.of(), List.of(), List.of(), List.of())));

        CodeSessionDTO session = service.open(new SessionRequest(CustomsArea.MUEBLES, "412345", 90001));

        assertThat(session.header().purchaseFraction()).isEqualTo("9403.60.99");
    }

    @Test
    void supplierWithoutBrandsIsFlaggedForBrandCreation() {
        when(psClient.brands(anyInt(), any())).thenReturn(ok(List.of()));

        assertThatThrownBy(() -> service.open(new SessionRequest(CustomsArea.ROPA, "412345", 90001)))
            .isInstanceOfSatisfying(NotFoundException.class,
                ex -> assertThat(ex.getStatus()).isEqualTo(NotFoundException.NO_BRANDS))
            .hasMessage(AppMessages.SIN_MARCAS);
        verify(psClient, never()).acquireLock(any());
    }

    @Test
    void codeInUseStopsBeforeLoading() {
        when(psClient.lockStatus(any())).thenReturn(ok(new LockStatusDTO(true)));

        assertThatThrownBy(() -> service.open(new SessionRequest(CustomsArea.MUEBLES, "412345", 90001)))
            .isInstanceOf(ConflictException.class)
            .hasMessage(AppMessages.CODIGO_EN_USO);
        verify(psClient, never()).snapshot(any(), anyInt());
        verify(psClient, never()).acquireLock(any());
    }

    @Test
    void codeWithoutInventoryIsRejected() {
        when(psClient.inventory(any(), anyInt())).thenReturn(ok(List.of()));

        assertThatThrownBy(() -> service.open(new SessionRequest(CustomsArea.MUEBLES, "412345", 90001)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(AppMessages.SIN_INVENTARIO);
    }

    private static <T> ApiResponseDTO<T> ok(T data) {
        return ApiResponseDTO.ok(data);
    }
}
