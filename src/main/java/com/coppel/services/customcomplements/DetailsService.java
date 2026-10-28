package com.coppel.services.customcomplements;

import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;
import com.coppel.dto.request.customcomplements.ExpirationRequest;
import com.coppel.dto.request.customcomplements.IepsRequest;
import com.coppel.dto.request.customcomplements.RegulationsRequest;
import com.coppel.dto.request.customcomplements.SanitaryNoticeRequest;
import com.coppel.dto.request.customcomplements.SizeRangesRequest;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import com.coppel.dto.response.customcomplements.RegulationOptionsDTO;
import com.coppel.dto.response.customcomplements.RegulationsResultDTO;
import com.coppel.dto.response.customcomplements.SanitaryNoticeResultDTO;
import com.coppel.dto.response.customcomplements.SizeRangesResultDTO;

/**
 * Subformularios por pieza / rango y del código (antes FrmRegulaciones, FrmAvisoSanitario, FrmEspecificaciones,
 * FrmIEPS y FrmRangoDeTallas).
 */
public interface DetailsService {

    RegulationOptionsDTO getRegulationOptions(String fraction);

    RegulationsResultDTO acceptRegulations(RegulationsRequest request);

    SanitaryNoticeResultDTO acceptSanitaryNotice(SanitaryNoticeRequest request);

    SpecificationRowDTO acceptSpecifications(SpecificationRowDTO request);

    IepsRowDTO acceptIeps(IepsRequest request);

    SizeRangesResultDTO acceptSizeRanges(SizeRangesRequest request);

    ExpirationStatusDTO checkExpiration(ExpirationRequest request);
}
