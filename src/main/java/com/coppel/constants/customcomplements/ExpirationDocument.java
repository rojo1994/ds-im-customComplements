package com.coppel.constants.customcomplements;

/**
 * {@code id_Documento} de RopaCalexico.dbo.Cat_DocumentosConAlertadeVencimiento con el que se llama a
 * dbo.fun_validarfechavencimiento.
 */
public enum ExpirationDocument {
    /** Aviso sanitario por aduana (ValidarFechaVencimientoEnAduanas). */
    SANITARY_NOTICE(1),
    /** NOM de cumplimiento (FrmRegulaciones.ValidarFechaNomCumplimiento). */
    NOM(2),
    /** Imagen de marca (FrmComplementosAduanales.ValidarFechaVencimiento). */
    BRAND_IMAGE(3),
    /** Certificado fitosanitario (FrmRegulaciones.ValidarFechaVencimientotoFito). */
    PHYTOSANITARY(6);

    private final int id;

    ExpirationDocument(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
