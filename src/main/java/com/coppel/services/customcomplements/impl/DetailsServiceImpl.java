package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;
import com.coppel.dto.request.customcomplements.ExpirationRequest;
import com.coppel.dto.request.customcomplements.IepsRequest;
import com.coppel.dto.request.customcomplements.RegulationsRequest;
import com.coppel.dto.request.customcomplements.SanitaryNoticeRequest;
import com.coppel.dto.request.customcomplements.SizeRangesRequest;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import com.coppel.dto.response.customcomplements.RegulationOptionDTO;
import com.coppel.dto.response.customcomplements.RegulationOptionsDTO;
import com.coppel.dto.response.customcomplements.RegulationsResultDTO;
import com.coppel.dto.response.customcomplements.SanitaryNoticeResultDTO;
import com.coppel.dto.response.customcomplements.SizeRangesResultDTO;
import com.coppel.exceptions.customcomplements.BadRequestException;
import com.coppel.services.customcomplements.DetailsService;
import com.coppel.services.customcomplements.impl.DetailRules.Applicability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.coppel.client.customcomplements.PsResponses.data;
import static com.coppel.client.customcomplements.PsResponses.list;
import static com.coppel.services.customcomplements.impl.DetailRules.COMPLIANCE_NOM;
import static com.coppel.services.customcomplements.impl.DetailRules.LABELING_NOM;
import static com.coppel.services.customcomplements.impl.DetailRules.PHYTOSANITARY;
import static com.coppel.services.customcomplements.impl.DetailRules.PROFEPA;

@Service
@RequiredArgsConstructor
public class DetailsServiceImpl implements DetailsService {

    private final CustomComplementsPsClient psClient;
    private final ExpirationChecker expirationChecker;

    /**
     * Antes FrmRegulaciones_Load: cada sección se habilita si la fracción la lleva (validarregulacion 2, 3, 4, 5) y
     * trae sus NOM y excepciones. Sin NOM asignadas el escritorio avisaba y cerraba el formulario.
     */
    @Override
    public RegulationOptionsDTO getRegulationOptions(String fraction) {
        String value = fraction == null ? "" : fraction.trim();
        if (value.isEmpty()) {
            throw new BadRequestException(AppMessages.REGISTRAR_INFORMACION_ADUANAL);
        }
        List<String> warnings = new ArrayList<>();
        RegulationOptionDTO profepa = option(value, PROFEPA, false);
        RegulationOptionDTO phytosanitary = option(value, PHYTOSANITARY, false);
        RegulationOptionDTO compliance = option(value, COMPLIANCE_NOM, true);
        RegulationOptionDTO labeling = option(value, LABELING_NOM, true);
        if (compliance.applies() && compliance.noms().isEmpty()) {
            warnings.add(AppMessages.SIN_NOMS_CUMPLIMIENTO);
        }
        if (labeling.applies() && labeling.noms().isEmpty()) {
            warnings.add(AppMessages.SIN_NOMS_ETIQUETADO);
        }
        return new RegulationOptionsDTO(profepa, phytosanitary, compliance, labeling, warnings);
    }

    @Override
    public RegulationsResultDTO acceptRegulations(RegulationsRequest request) {
        String fraction = request.fraction().trim();
        Applicability applies = new Applicability(applies(fraction, PHYTOSANITARY), applies(fraction, PROFEPA),
            applies(fraction, COMPLIANCE_NOM), applies(fraction, LABELING_NOM));
        return new RegulationsResultDTO(DetailRules.regulations(request, applies, expirationChecker::check));
    }

    @Override
    public SanitaryNoticeResultDTO acceptSanitaryNotice(SanitaryNoticeRequest request) {
        return DetailRules.sanitaryNotice(request, expirationChecker::check);
    }

    @Override
    public SpecificationRowDTO acceptSpecifications(SpecificationRowDTO request) {
        return DetailRules.specifications(request);
    }

    @Override
    public IepsRowDTO acceptIeps(IepsRequest request) {
        if (request.classId() == null || request.classId() <= 0) {
            throw new BadRequestException(AppMessages.SELECCIONAR_CLASE);
        }
        return DetailRules.ieps(request, list(psClient.iepsCategories(request.classId())), list(psClient.iepsUnits()));
    }

    @Override
    public SizeRangesResultDTO acceptSizeRanges(SizeRangesRequest request) {
        List<SizeRowDTO> sizes = DetailRules.sizeRanges(request.sizes());
        return new SizeRangesResultDTO(sizes, DetailRules.componentsFromSizes(sizes, request.representativePiece(),
            request.representativeRange()));
    }

    @Override
    public ExpirationStatusDTO checkExpiration(ExpirationRequest request) {
        return expirationChecker.check(request.date(), request.document());
    }

    private RegulationOptionDTO option(String fraction, int regulation, boolean withNoms) {
        if (!applies(fraction, regulation)) {
            return new RegulationOptionDTO(false, List.of(), List.of());
        }
        List<CatalogItemDTO> noms = withNoms ? list(psClient.noms(fraction, regulation)) : List.of();
        return new RegulationOptionDTO(true, noms, list(psClient.exceptions(regulation)));
    }

    private boolean applies(String fraction, int regulation) {
        return data(psClient.regulationApplies(fraction, regulation)).applies();
    }
}
