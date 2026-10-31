package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.CentralDataDTO;
import com.coppel.client.customcomplements.dto.FurnitureCentralRequest;
import com.coppel.client.customcomplements.dto.CommitGeneralRequest;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.request.customcomplements.SaveComplementsRequest;
import com.coppel.dto.request.customcomplements.SaveHeaderInput;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaveRulesTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 27);

    @Test
    void everyComponentNeedsCustomsInformation() {
        SaveComplementsRequest request = request(CustomsArea.MUEBLES, header(false, null),
            List.of(new ComponentDTO(1, 0, "", true), new ComponentDTO(2, 0, "", false)), List.of(customs(1, 0, "2.50")));

        assertThatThrownBy(() -> SaveRules.validate(request, SaveRules.byKey(request.customs()), TODAY,
            ExpirationStatus.VALID)).hasMessage(AppMessages.INFORMACION_ADUANAL_COMPONENTES);
    }

    @Test
    void combinedNeedsGeneralDescription() {
        SaveComplementsRequest request = request(CustomsArea.MUEBLES, header(true, 2),
            List.of(new ComponentDTO(1, 0, "", true), new ComponentDTO(2, 0, "", false)),
            List.of(customs(1, 0, "1.00"), customs(2, 0, "1.50")));

        assertThatThrownBy(() -> SaveRules.validate(request, SaveRules.byKey(request.customs()), TODAY,
            ExpirationStatus.VALID)).hasMessage(AppMessages.CAPTURAR_DESCRIPCION_GENERAL);
    }

    @Test
    void costIsComparedRoundedToCents() {
        SaveHeaderInput header = header(false, null);
        assertThat(SaveRules.sameCost(header, List.of(customs(1, 0, "1.004"), customs(2, 0, "1.50")))).isTrue();
        assertThat(SaveRules.sameCost(header, List.of(customs(1, 0, "1.00")))).isFalse();
    }

    @Test
    void generalSumsPiecesAndTruncatesQuota() {
        SaveComplementsRequest request = request(CustomsArea.MUEBLES, header(false, null),
            List.of(new ComponentDTO(1, 0, "", true)), List.of());
        CommitGeneralRequest general = SaveRules.general(request,
            List.of(customs(1, 0, "1.00", new BigDecimal("1.239")), customs(2, 0, "1.50", new BigDecimal("1.009"))), true);

        assertThat(general.componentPieces()).isEqualTo(2);
        assertThat(general.compensatoryQuota()).isEqualByComparingTo("2.24");
        assertThat(general.generalDescription()).isEmpty();
        assertThat(general.requiresSanitaryNotice()).isTrue();
    }

    @Test
    void furnitureCentralMultipliesBy100() {
        CentralDataDTO data = central(1);
        FurnitureCentralRequest request = SaveRules.furnitureCentral(90001, data);

        assertThat(request.codigo()).isEqualTo(90001);
        assertThat(request.arancelFrontera()).isEqualByComparingTo("1550");
        assertThat(request.cuotaCompensatoria()).isEqualTo(123);
        assertThat(request.clavePais()).isEqualTo("CN");
    }

    @Test
    void clothingCentralSendsTariffValueOnlyForUnit4() {
        assertThat(SaveRules.clothingCentral(90001, central(4)).valorTarifa()).isEqualByComparingTo("2.57");
        assertThat(SaveRules.clothingCentral(90001, central(6)).valorTarifa()).isEqualByComparingTo("0");
    }

    private static CentralDataDTO central(int tariffUnit) {
        BigDecimal zero = BigDecimal.ZERO;
        return new CentralDataDTO("6109.10.01", new BigDecimal("15.50"), new BigDecimal("20"), 6, false, false, 0, 0,
            tariffUnit, "CN", true, 1, new BigDecimal("1.239"), new BigDecimal("2.5678"), false, 1, zero, "", "", 0,
            false, zero, false, false, 0, 0, false, LocalDate.of(1900, 1, 1), "", LocalDate.of(1900, 1, 1), 0, false, "",
            "", "", "", "", "");
    }

    private static SaveHeaderInput header(boolean combined, Integer count) {
        return new SaveHeaderInput(10, 1, 5, 1, "MODELO", true, false, false, null, false, combined, count, "",
            new BigDecimal("2.50"), 1, 0);
    }

    private static SaveComplementsRequest request(CustomsArea area, SaveHeaderInput header,
                                                  List<ComponentDTO> components, List<CustomsRowDTO> customs) {
        return new SaveComplementsRequest(area, 123456, 90001, header, components, customs, null, null, null, null, null,
            null, null, false);
    }

    private static CustomsRowDTO customs(int piece, int range, String cost) {
        return customs(piece, range, cost, BigDecimal.ZERO);
    }

    private static CustomsRowDTO customs(int piece, int range, String cost, BigDecimal quota) {
        BigDecimal zero = BigDecimal.ZERO;
        return new CustomsRowDTO(piece, range, "9403.60.99", false, "MUEBLE", "", "", "FURNITURE", 12, "CHINA",
            "9403.60.8081", new BigDecimal(cost), zero, false, zero, 1, BigDecimal.ONE, "1", 6, 6, 1, BigDecimal.ONE, zero,
            true, 1, zero, false, false, quota, false, 0, false, false, 0, 0, zero, zero, zero, zero, zero, zero, "",
            false);
    }
}
