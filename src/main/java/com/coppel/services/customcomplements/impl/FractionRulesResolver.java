package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.FractionMxnDTO;
import com.coppel.dto.response.customcomplements.FractionRulesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.coppel.client.customcomplements.PsResponses.data;

/**
 * Lo que el escritorio consultaba de cada fracción MXN: validarfraccionmxn (existe, decreto, unidad de tarifa,
 * azúcar), validarregulacion (¿regulaciones?), validarregulacion 1 (¿aviso sanitario?) y validarieps.
 */
@Component
@RequiredArgsConstructor
public class FractionRulesResolver {

    static final int SANITARY_NOTICE = 1;

    private final CustomComplementsPsClient psClient;

    public FractionRulesDTO resolve(String fraction) {
        FractionMxnDTO mxn = data(psClient.validateMxn(fraction));
        if (!mxn.exists()) {
            return new FractionRulesDTO(fraction, false, false, 0, "", false, false, false, false);
        }
        boolean regulations = data(psClient.regulationApplies(fraction, null)).applies();
        boolean sanitaryNotice = data(psClient.regulationApplies(fraction, SANITARY_NOTICE)).applies();
        boolean ieps = data(psClient.requiresIeps(fraction)).required();
        return new FractionRulesDTO(fraction, true, mxn.appliesDecree(),
            mxn.tariffUnitId() == null ? 0 : mxn.tariffUnitId(), mxn.tariffUnitName(), mxn.containsSugar(),
            regulations, ieps, sanitaryNotice);
    }
}
