package com.coppel.client.customcomplements;

import com.coppel.client.customcomplements.dto.ClothingCentralRequest;
import com.coppel.client.customcomplements.dto.FurnitureCentralRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Cliente de ps-tms-fmd-inventarios-centrales: copia de los complementos en Central Muebles / Central Ropa (antes
 * GrabarCentral con las conexiones {@code CadenaConexionServerCentral*} del escritorio).
 */
public interface InventoryCentersClient {

    /** MaestroMuebles.dbo.proc_mxtrf010grabarcentralmuebles. */
    @PostExchange("/custom-complements-furniture")
    void saveFurniture(@RequestBody FurnitureCentralRequest request);

    /** comprasropa.dbo.proc_mxtrf010grabarcentralropa. */
    @PostExchange("/custom-complements-clothing")
    void saveClothing(@RequestBody ClothingCentralRequest request);
}
