package com.coppel.services.customcomplements;

import com.coppel.client.customcomplements.dto.TariffsDTO;
import com.coppel.dto.request.customcomplements.AutomaticPermitCheckRequest;
import com.coppel.dto.request.customcomplements.CustomsInfoRequest;
import com.coppel.dto.request.customcomplements.FractionCheckRequest;
import com.coppel.dto.response.customcomplements.AutomaticPermitResultDTO;
import com.coppel.dto.response.customcomplements.CustomsInfoResultDTO;
import com.coppel.dto.response.customcomplements.FractionCheckDTO;

/**
 * Información aduanal de una pieza / rango (antes FrmAduanas).
 */
public interface CustomsInfoService {

    FractionCheckDTO checkFraction(FractionCheckRequest request);

    TariffsDTO getTariffs(String fraction, int countryId);

    void validateUsaFraction(String fraction);

    AutomaticPermitResultDTO automaticPermit(AutomaticPermitCheckRequest request);

    CustomsInfoResultDTO accept(CustomsInfoRequest request);
}
