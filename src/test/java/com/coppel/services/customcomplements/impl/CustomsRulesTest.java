package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.PermitColorRangeDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.exceptions.customcomplements.BadRequestException;
import com.coppel.services.customcomplements.impl.CustomsRules.Context;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Reglas de FrmAduanas / AduanasDAL con los mismos valores que calculaba el escritorio.
 */
class CustomsRulesTest {

    @Test
    void weightIsConvertedToKilograms() {
        assertThat(CustomsRules.weightInKg(1, new BigDecimal("1.5000"))).isEqualTo("1.5");
        assertThat(CustomsRules.weightInKg(2, new BigDecimal("250"))).isEqualTo("0.25");
        assertThat(CustomsRules.weightInKg(3, new BigDecimal("2.205"))).isEqualTo("1");
        assertThat(CustomsRules.weightInKg(4, new BigDecimal("35.274"))).isEqualTo("1");
    }

    @Test
    void componentPiecesFollowTheCommercialUnit() {
        assertThat(CustomsRules.componentPieces(12, 1, 6)).isEqualTo(6);
        assertThat(CustomsRules.componentPieces(9, 6, 0)).isEqualTo(2);
        assertThat(CustomsRules.componentPieces(9, 1, 0)).isEqualTo(1);
        assertThat(CustomsRules.componentPieces(3, 6, 4)).isEqualTo(1);
    }

    @Test
    void preferentialTariffAppliesOnlyWhenItIsLower() {
        assertThat(CustomsRules.applyTariff(new BigDecimal("20.00"), "5.00", true)).isEqualByComparingTo("5.00");
        assertThat(CustomsRules.applyTariff(new BigDecimal("3.00"), "5.00", true)).isEqualByComparingTo("3.00");
        assertThat(CustomsRules.applyTariff(new BigDecimal("20.00"), "N/A", true)).isEqualByComparingTo("20.00");
        assertThat(CustomsRules.applyTariff(new BigDecimal("20.00"), "5.00", false)).isEqualByComparingTo("20.00");
    }

    @Test
    void compensatoryQuotaPerPiece() {
        // costo por pieza 2.00 contra referencia 3.50 → (3.50 − 2.00) × 4
        assertThat(CustomsRules.compensatoryQuota(true, false, new BigDecimal("3.50"), new BigDecimal("8.00"), 4,
            BigDecimal.ONE)).isEqualByComparingTo("6.0000");
        // costo por pieza mayor a la referencia
        assertThat(CustomsRules.compensatoryQuota(true, false, new BigDecimal("1.00"), new BigDecimal("8.00"), 4,
            BigDecimal.ONE)).isEqualByComparingTo("0");
        // costo de referencia específico: se toma la referencia (4 decimales, truncada)
        assertThat(CustomsRules.compensatoryQuota(true, true, new BigDecimal("3.123456"), new BigDecimal("8.00"), 4,
            BigDecimal.ONE)).isEqualByComparingTo("3.1234");
    }

    @Test
    void compensatoryQuotaPerTariffUnit() {
        // costo / valor UMT = 2.00 contra referencia 2.50 → (2.50 − 2.00) × 3
        assertThat(CustomsRules.compensatoryQuota(false, false, new BigDecimal("2.50"), new BigDecimal("6.00"), 1,
            new BigDecimal("3"))).isEqualByComparingTo("1.5000");
    }

    @Test
    void colorComesFromThePercentageRange() {
        List<PermitColorRangeDTO> ranges = List.of(
            new PermitColorRangeDTO(BigDecimal.ZERO, new BigDecimal("10"), "verde"),
            new PermitColorRangeDTO(new BigDecimal("10.01"), new BigDecimal("100"), "rojo"));
        assertThat(CustomsRules.color(new BigDecimal("5.5"), ranges)).isEqualTo("verde");
        assertThat(CustomsRules.color(new BigDecimal("50"), ranges)).isEqualTo("rojo");
    }

    @Test
    void fractionMustMatchTheMask() {
        assertThat(CustomsRules.matchesMask("6109.10.01", "0000.00.00")).isTrue();
        assertThat(CustomsRules.matchesMask("6109.10.0", "0000.00.00")).isFalse();
        assertThat(CustomsRules.matchesMask("6109 10 01", "0000.00.00")).isFalse();
        assertThat(CustomsRules.matchesMask("6109.10.0012", "####.##.####")).isTrue();
    }

    @Test
    void validationFollowsTheScreenOrder() {
        CustomsRowDTO noDescription = row("", "T-SHIRT", 0);
        assertThatThrownBy(() -> CustomsRules.validate(noDescription, new Context(false, false, false)))
            .isInstanceOf(BadRequestException.class)
            .hasMessage(AppMessages.CAPTURAR_DESCRIPCION_ADUANAL);

        CustomsRowDTO piecesUnit = row("PLAYERA", "T-SHIRT", 1);
        assertThatThrownBy(() -> CustomsRules.validate(piecesUnit, new Context(false, false, false)))
            .hasMessage(AppMessages.PIEZAS_MAYOR_A_1);

        CustomsRowDTO noCause = row("PLAYERA", "T-SHIRT", 2);
        assertThatThrownBy(() -> CustomsRules.validate(noCause, new Context(false, true, false)))
            .hasMessage(AppMessages.SELECCIONAR_RAZON_NO_ARANCEL);
    }

    /** UMC 12 (piezas) con las piezas indicadas y sin arancel preferencial marcado. */
    private static CustomsRowDTO row(String description, String english, int pieces) {
        BigDecimal zero = BigDecimal.ZERO;
        return new CustomsRowDTO(1, 0, "6109.10.01", false, description, "", "", english, 12, "CHINA", "6109.10.0012",
            new BigDecimal("2.50"), zero, false, zero, 1, new BigDecimal("0.2"), "", 6, 12, pieces,
            new BigDecimal("1"), zero, false, 0, zero, false, false, zero, false, 0, false, false, 0, 0, zero, zero, zero,
            zero, zero, zero, "", false);
    }
}
