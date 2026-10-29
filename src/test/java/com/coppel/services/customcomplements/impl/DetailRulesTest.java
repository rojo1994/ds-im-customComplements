package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.IepsCategoryDTO;
import com.coppel.client.customcomplements.dto.IepsUnitDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.request.customcomplements.IepsRequest;
import com.coppel.dto.request.customcomplements.NomInput;
import com.coppel.dto.request.customcomplements.RegulationsRequest;
import com.coppel.dto.request.customcomplements.SanitaryNoticeRequest;
import com.coppel.dto.request.customcomplements.SanitaryOfficeInput;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import com.coppel.dto.response.customcomplements.SanitaryNoticeResultDTO;
import com.coppel.services.customcomplements.impl.DetailRules.Applicability;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DetailRulesTest {

    private static final LocalDate DATE = LocalDate.of(2027, 5, 1);

    @Test
    void complianceNomNeedsCertificateOrException() {
        RegulationsRequest request = new RegulationsRequest(1, 0, "8516.40.01", null, null, null, null, false,
            List.of(new NomInput(10, true, "", null, null)), List.of());
        Applicability applies = new Applicability(false, false, true, false);

        assertThatThrownBy(() -> DetailRules.regulations(request, applies, (date, document) -> ExpirationStatusDTO.valid()))
            .hasMessage(AppMessages.CAPTURAR_CERTIFICADO_O_EXCEPCION);
    }

    @Test
    void expiredPhytosanitaryCertificateIsRejected() {
        RegulationsRequest request = new RegulationsRequest(1, 0, "0602.10.01", null, "CERT-1", DATE, null, false,
            List.of(), List.of());
        Applicability applies = new Applicability(true, false, false, false);

        assertThatThrownBy(() -> DetailRules.regulations(request, applies,
            (date, document) -> new ExpirationStatusDTO(ExpirationStatus.EXPIRED, "Certificado está vencido.")))
            .hasMessage(AppMessages.FITOSANITARIO_VENCIDO);
    }

    @Test
    void regulationRowsAreBuiltLikeLlenarTemporales() {
        RegulationsRequest request = new RegulationsRequest(2, 1, "8516.40.01", 7, null, null, null, true,
            List.of(new NomInput(10, true, "CERT", DATE, null)), List.of(new NomInput(20, true, "", null, 3),
            new NomInput(21, false, "", null, null)));
        Applicability applies = new Applicability(false, true, true, true);

        List<RegulationRowDTO> rows = DetailRules.regulations(request, applies,
            (date, document) -> ExpirationStatusDTO.valid());

        assertThat(rows).extracting(RegulationRowDTO::regulation, RegulationRowDTO::nomOrOffice,
                RegulationRowDTO::exceptionId, RegulationRowDTO::nomInProcess)
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple(3, 0, 7, false),
                org.assertj.core.groups.Tuple.tuple(5, 20, 3, false),
                org.assertj.core.groups.Tuple.tuple(4, 0, 0, true),
                org.assertj.core.groups.Tuple.tuple(4, 10, 0, false));
        assertThat(rows).allMatch(row -> row.piece() == 2 && row.sizeRange() == 1);
    }

    @Test
    void sanitaryNoticeSummarizesExpiredAndExpiringOffices() {
        SanitaryNoticeRequest request = new SanitaryNoticeRequest(1, 0, "2106.90.99", List.of(
            new SanitaryOfficeInput(1, "TIJUANA", true, "F-1", DATE, null),
            new SanitaryOfficeInput(2, "MEXICALI", true, "F-2", DATE.plusDays(1), null),
            new SanitaryOfficeInput(3, "NOGALES", true, "", null, 4),
            new SanitaryOfficeInput(4, "TECATE", false, "", null, null)));

        SanitaryNoticeResultDTO result = DetailRules.sanitaryNotice(request, (date, document) -> date.equals(DATE)
            ? new ExpirationStatusDTO(ExpirationStatus.EXPIRED, "Aviso sanitario está vencido.")
            : new ExpirationStatusDTO(ExpirationStatus.EXPIRING,
                "Está por vencer la vigencia de Aviso sanitario. Quedan 5 días"));

        assertThat(result.rows()).hasSize(3);
        assertThat(result.rows().get(2).exceptionId()).isEqualTo(4);
        assertThat(result.message())
            .startsWith("El aviso sanitario está vencido para las siguientes aduanas: TIJUANA.")
            .contains("Está por vencer la vigencia del aviso sanitario para las siguientes aduanas: MEXICALI - 5 días.");
    }

    @Test
    void sanitaryNoticeNeedsAnOffice() {
        SanitaryNoticeRequest request = new SanitaryNoticeRequest(1, 0, "", List.of(
            new SanitaryOfficeInput(1, "TIJUANA", false, "", null, null)));

        assertThatThrownBy(() -> DetailRules.sanitaryNotice(request, (date, document) -> ExpirationStatusDTO.valid()))
            .hasMessage(AppMessages.SELECCIONAR_ADUANA);
    }

    @Test
    void iepsTakesPercentageAndCostFromTheCategory() {
        IepsRowDTO row = DetailRules.ieps(new IepsRequest(1, 0, 2, 5, 6, new BigDecimal("12.9")),
            List.of(new IepsCategoryDTO(5, "BEBIDAS", new BigDecimal("8.00"), new BigDecimal("1.250"), 6)),
            List.of(new IepsUnitDTO(6, "PIEZA", false)));

        assertThat(row.categoryPercentage()).isEqualByComparingTo("8.00");
        assertThat(row.additionalCost()).isEqualByComparingTo("1.250");
        assertThat(row.quantity()).isEqualByComparingTo("12");
    }

    @Test
    void sizeRangesMustBeConsecutive() {
        List<SizeRowDTO> sizes = List.of(new SizeRowDTO(1, 22, 1), new SizeRowDTO(1, 23, 3));

        assertThatThrownBy(() -> DetailRules.sizeRanges(sizes)).hasMessage(AppMessages.RANGO_CONSECUTIVO);
    }

    @Test
    void componentsAreRebuiltFromSizeRanges() {
        List<SizeRowDTO> sizes = DetailRules.sizeRanges(List.of(new SizeRowDTO(1, 22, 1), new SizeRowDTO(1, 23, 1),
            new SizeRowDTO(1, 24, 2), new SizeRowDTO(2, 22, 1)));

        List<ComponentDTO> components = DetailRules.componentsFromSizes(sizes, 1, 2);

        assertThat(components).containsExactly(new ComponentDTO(1, 1, "22-23", false), new ComponentDTO(1, 2, "24", true),
            new ComponentDTO(2, 1, "22", false));
    }
}
