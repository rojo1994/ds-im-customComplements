package com.coppel.services.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.IepsCategoryDTO;
import com.coppel.client.customcomplements.dto.NonTariffCauseDTO;
import com.coppel.dto.request.customcomplements.BrandInput;
import com.coppel.dto.request.customcomplements.CauseInput;
import com.coppel.dto.response.customcomplements.BrandCreationDTO;
import com.coppel.dto.response.customcomplements.ScreenCatalogsDTO;

import java.util.List;

/**
 * Catálogos de la pantalla, alta de marca y causas para no aplicar arancel preferencial.
 */
public interface CatalogService {

    ScreenCatalogsDTO getScreenCatalogs();

    List<IepsCategoryDTO> getIepsCategories(int classId);

    List<CatalogItemDTO> getExceptions(int regulation);

    List<CatalogItemDTO> getNoms(String fraction, int regulation);

    List<NonTariffCauseDTO> getCauses(boolean all);

    void createCause(CauseInput input);

    void renameCause(long causeId, CauseInput input);

    void toggleCause(long causeId);

    BrandCreationDTO createBrand(BrandInput input);
}
