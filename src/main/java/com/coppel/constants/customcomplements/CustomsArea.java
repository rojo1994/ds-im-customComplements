package com.coppel.constants.customcomplements;

/**
 * Radio Área de FrmComplementosAduanales. {@code code} es el {@code @area} de los SP de Pack/ (Muebles 1, Ropa 2;
 * el escritorio abría con Muebles marcado).
 */
public enum CustomsArea {
    MUEBLES(1, "Muebles"),
    ROPA(2, "Ropa");

    private final int code;
    private final String label;

    CustomsArea(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int code() {
        return code;
    }

    public String label() {
        return label;
    }

    public boolean isClothing() {
        return this == ROPA;
    }
}
