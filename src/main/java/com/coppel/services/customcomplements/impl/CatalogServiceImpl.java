package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.dto.BrandCreatedDTO;
import com.coppel.client.customcomplements.dto.BrandRequest;
import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.CauseRequest;
import com.coppel.client.customcomplements.dto.IepsCategoryDTO;
import com.coppel.client.customcomplements.dto.NonTariffCauseDTO;
import com.coppel.client.customcomplements.dto.SupplierDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.request.customcomplements.BrandInput;
import com.coppel.dto.request.customcomplements.CauseInput;
import com.coppel.dto.response.customcomplements.BrandCreationDTO;
import com.coppel.dto.response.customcomplements.ScreenCatalogsDTO;
import com.coppel.exceptions.customcomplements.ConflictException;
import com.coppel.exceptions.customcomplements.NotFoundException;
import com.coppel.services.customcomplements.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.coppel.client.customcomplements.PsResponses.data;
import static com.coppel.client.customcomplements.PsResponses.list;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    static final int SANITARY_NOTICE = 1;

    private final CustomComplementsPsClient psClient;

    /**
     * Antes FrmComplementosAduanales_Load (importadoras; sin importadoras la opción no abría) más los combos que cada
     * subformulario cargaba al abrirse. Los demás catálogos vacíos los avisa el subformulario que los usa.
     */
    @Override
    public ScreenCatalogsDTO getScreenCatalogs() {
        List<CatalogItemDTO> importers = list(psClient.importers());
        if (importers.isEmpty()) {
            throw new ConflictException(AppMessages.SIN_IMPORTADORAS);
        }
        return new ScreenCatalogsDTO(importers, list(psClient.countries()), list(psClient.weightUnits()),
            list(psClient.commercialUnits()), list(psClient.quotaTypes()), list(psClient.producerTypes()),
            list(psClient.preferentialCriteria()), data(psClient.fractionMask()).mask(), CustomsRules.USA_FRACTION_MASK,
            list(psClient.customsOffices()), list(psClient.exceptions(SANITARY_NOTICE)), list(psClient.iepsClasses()),
            list(psClient.iepsUnits()), list(psClient.energyUnits()), list(psClient.permitColorRanges()),
            list(psClient.causes(false)), list(psClient.brandTypes()));
    }

    @Override
    public List<IepsCategoryDTO> getIepsCategories(int classId) {
        return list(psClient.iepsCategories(classId));
    }

    @Override
    public List<CatalogItemDTO> getExceptions(int regulation) {
        return list(psClient.exceptions(regulation));
    }

    @Override
    public List<CatalogItemDTO> getNoms(String fraction, int regulation) {
        return list(psClient.noms(fraction, regulation));
    }

    @Override
    public List<NonTariffCauseDTO> getCauses(boolean all) {
        return list(psClient.causes(all));
    }

    /** Antes AgregarCausa: "La causa ingresada ya existe en el catalogo". */
    @Override
    public void createCause(CauseInput input) {
        if (data(psClient.createCause(new CauseRequest(input.description().trim()))).duplicated()) {
            throw new ConflictException(AppMessages.CAUSA_EXISTENTE);
        }
    }

    /** Antes ActualizarDescripcionCausa. */
    @Override
    public void renameCause(long causeId, CauseInput input) {
        if (data(psClient.renameCause(causeId, new CauseRequest(input.description().trim()))).duplicated()) {
            throw new ConflictException(AppMessages.DESCRIPCION_CAUSA_EXISTENTE);
        }
    }

    /**
     * Alta de marca para el proveedor del código en su área (el front anterior mandaba el proveedor y ps-tms grababa
     * siempre en Muebles). Regresa el combo de marcas actualizado.
     */
    @Override
    public BrandCreationDTO createBrand(BrandInput input) {
        SupplierDTO supplier;
        try {
            supplier = data(psClient.supplier(input.area(), input.codeNumber()));
        } catch (NotFoundException ex) {
            throw new NotFoundException(AppMessages.SIN_PROVEEDOR);
        }
        BrandCreatedDTO created = data(psClient.createBrand(
            new BrandRequest(input.area(), supplier.id(), input.brandTypeId(), input.description())));
        if (created.duplicated()) {
            throw new ConflictException(AppMessages.MARCA_EXISTENTE);
        }
        return new BrandCreationDTO(created.brandId(), list(psClient.brands(supplier.id(), input.area())));
    }

    /** Antes ActualizarEstadoCausas (cada causa marcada en Baja). */
    @Override
    public void toggleCause(long causeId) {
        psClient.toggleCause(causeId);
    }
}
