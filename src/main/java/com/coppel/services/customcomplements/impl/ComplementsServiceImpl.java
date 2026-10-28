package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.CentralDataDTO;
import com.coppel.client.customcomplements.dto.ImpiRequest;
import com.coppel.client.customcomplements.dto.LockRequest;
import com.coppel.client.customcomplements.dto.WorkspaceCommitRequest;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.constants.customcomplements.ExpirationDocument;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.request.customcomplements.ImpiPieceInput;
import com.coppel.dto.request.customcomplements.SaveComplementsRequest;
import com.coppel.dto.request.customcomplements.SaveHeaderInput;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import com.coppel.dto.response.customcomplements.SaveResultDTO;
import com.coppel.exceptions.customcomplements.BusinessException;
import com.coppel.exceptions.customcomplements.ConflictException;
import com.coppel.exceptions.customcomplements.RemoteServiceException;
import com.coppel.services.customcomplements.ComplementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coppel.client.customcomplements.PsResponses.data;

/**
 * Guardar con el orden del escritorio: validar → costos → grabarcomplementos (commit en ps) → borrar IMPI si cambió
 * la marca o alguna fracción → obtenerdatoscomplementos → servidor central → soltar el código.
 */
@Service
@RequiredArgsConstructor
public class ComplementsServiceImpl implements ComplementsService {

    private final CustomComplementsPsClient psClient;
    private final FractionRulesResolver fractionRules;
    private final ExpirationChecker expirationChecker;
    private final Clock clock;

    @Override
    public SaveResultDTO save(SaveComplementsRequest request) {
        CustomsArea area = request.area();
        int code = request.code();
        SaveHeaderInput header = request.header();

        // Sólo la información aduanal de los renglones del grid (las piezas borradas ya no viajan).
        Map<String, CustomsRowDTO> customsByKey = SaveRules.byKey(request.customs());
        List<CustomsRowDTO> customs = request.components().stream()
            .map(component -> customsByKey.get(SaveRules.key(component.piece(), component.sizeRange())))
            .filter(Objects::nonNull)
            .toList();

        ExpirationStatus brandImageStatus = header.brandImage()
            ? expirationChecker.check(header.brandImageExpiration(), ExpirationDocument.BRAND_IMAGE).status()
            : ExpirationStatus.VALID;
        SaveRules.validate(request, customsByKey, LocalDate.now(clock), brandImageStatus);
        if (!request.confirmCostDifference() && !SaveRules.sameCost(header, customs)) {
            throw new ConflictException(ConflictException.COST_DIFFERENCE, AppMessages.DIFERENCIA_COSTOS);
        }

        // Fracción que lleva aviso sanitario (antes VerificarAvisoSanitario: la del primer componente que lo lleva).
        String noticeFraction = customs.stream()
            .map(CustomsRowDTO::fraction)
            .filter(fraction -> fractionRules.resolve(fraction).sanitaryNotice())
            .findFirst()
            .orElse("");
        boolean requiresSanitaryNotice = !noticeFraction.isEmpty();

        List<RegulationRowDTO> regulations = new ArrayList<>(request.regulations().stream()
            .filter(row -> belongsToGrid(row, request.components()))
            .toList());
        if (requiresSanitaryNotice) {
            regulations.addAll(SaveRules.sanitaryNotice(request.sanitaryNotice(), header, noticeFraction));
        }
        WorkspaceCommitRequest commit = new WorkspaceCommitRequest(
            SaveRules.general(request, customs, requiresSanitaryNotice), customs,
            area.isClothing() ? request.sizes() : List.of(), regulations,
            request.specifications().stream().filter(row -> belongsToGrid(row.piece(), row.sizeRange(),
                request.components())).toList(),
            request.ieps().stream().filter(row -> belongsToGrid(row.piece(), row.sizeRange(),
                request.components())).toList());
        try {
            psClient.commit(area, code, commit);
        } catch (BusinessException ex) {
            throw new RemoteServiceException(AppMessages.ERROR_GUARDAR, ex);
        }

        deleteImpi(request);
        saveCentral(area, code);
        psClient.releaseLock(new LockRequest(area, code, request.employee()));
        return new SaveResultDTO(true);
    }

    /** Antes EliminarDatosImpi: por la marca anterior o por cada pieza cuya fracción cambió. */
    private void deleteImpi(SaveComplementsRequest request) {
        if (request.previousBrandId() != null && request.previousBrandId() > 0) {
            psClient.deleteImpi(request.area(), request.code(),
                new ImpiRequest(String.valueOf(request.previousBrandId()), 0, 0, "0"));
            return;
        }
        for (ImpiPieceInput piece : request.impiPieces()) {
            psClient.deleteImpi(request.area(), request.code(), new ImpiRequest("0", piece.piece(), piece.sizeRange(),
                piece.previousFraction() == null ? "" : piece.previousFraction().trim()));
        }
    }

    /** Antes ObtenerDatosCentral + GrabarCentral con el mensaje de cada servidor. */
    private void saveCentral(CustomsArea area, int code) {
        CentralDataDTO data;
        try {
            data = data(psClient.centralData(area, code));
        } catch (BusinessException ex) {
            throw new RemoteServiceException(AppMessages.SIN_DATOS_CENTRAL, ex);
        }
        try {
            psClient.saveCentral(area, code, SaveRules.central(area, data));
        } catch (BusinessException ex) {
            throw new RemoteServiceException(area.isClothing() ? AppMessages.ERROR_CENTRAL_ROPA
                : AppMessages.ERROR_CENTRAL_MUEBLES, ex);
        }
    }

    private static boolean belongsToGrid(RegulationRowDTO row, List<ComponentDTO> components) {
        return belongsToGrid(row.piece(), row.sizeRange(), components);
    }

    private static boolean belongsToGrid(int piece, int sizeRange, List<ComponentDTO> components) {
        return components.stream().anyMatch(component -> component.piece() == piece && component.sizeRange() == sizeRange);
    }
}
