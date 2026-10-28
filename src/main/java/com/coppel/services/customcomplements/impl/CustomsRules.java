package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.PermitColorRangeDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.exceptions.customcomplements.BadRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Reglas de Información aduanal (antes FrmAduanas y AduanasDAL). Sin estado: todo lo que consultaba a la base llega
 * como parámetro.
 */
final class CustomsRules {

    /** Unidad comercial PIEZAS (cmbUMC "12"): se capturan las piezas que componen. */
    static final int UMC_PIECES = 12;
    /** Unidad comercial "9" con unidad de tarifa "6": dos piezas. */
    static final int UMC_PAIR = 9;
    static final int TARIFF_UNIT_PAIR = 6;
    /** Unidad de tarifa "4": el valor UMT se manda al central de Ropa. */
    static final int TARIFF_UNIT_SQUARE_METERS = 4;
    /** Tipo de cuota cuyo nombre es PIEZA (cmbTipoDeCuota.Text.Trim().Equals("PIEZA")). */
    static final String QUOTA_PER_PIECE = "PIEZA";
    static final String NOT_APPLICABLE = "N/A";
    static final BigDecimal MAX_QUOTA = new BigDecimal("99999.9999");
    static final String USA_FRACTION_MASK = "####.##.####";

    private CustomsRules() {
    }

    /**
     * Antes el {@code switch (IdMedidaPeso)} de AduanasDAL.GuardarAduanas: 1 kilogramo, 2 gramo, 3 libra, 4 onza.
     * Se graba como texto {@code "0.####"} en peso_kg.
     */
    static String weightInKg(int weightUnitId, BigDecimal weight) {
        BigDecimal value = weight == null ? BigDecimal.ZERO : weight;
        BigDecimal kilos = switch (weightUnitId) {
            case 1 -> value;
            case 2 -> value.divide(new BigDecimal("1000.00"), 10, RoundingMode.HALF_UP);
            case 3 -> value.divide(new BigDecimal("2.205"), 10, RoundingMode.HALF_UP);
            case 4 -> value.divide(new BigDecimal("35.274"), 10, RoundingMode.HALF_UP);
            default -> BigDecimal.ZERO;
        };
        DecimalFormat format = new DecimalFormat("0.####", DecimalFormatSymbols.getInstance(Locale.ROOT));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(kilos);
    }

    /**
     * Antes ValidarPiezasComponen: con UMC 12 se capturan; con UMC 9 y unidad de tarifa 6 son 2; en cualquier otro
     * caso 1.
     */
    static int componentPieces(int commercialUnitId, int tariffUnitId, int captured) {
        if (commercialUnitId == UMC_PIECES) {
            return captured;
        }
        return commercialUnitId == UMC_PAIR && tariffUnitId == TARIFF_UNIT_PAIR ? 2 : 1;
    }

    /** Arancel a aplicar (antes ObtenerAplicar): el preferencial si aplica y es menor o igual al normal. */
    static BigDecimal applyTariff(BigDecimal normal, String preferential, boolean preferentialChecked) {
        BigDecimal base = normal == null ? BigDecimal.ZERO : normal;
        if (!preferentialChecked || preferential == null || NOT_APPLICABLE.equals(preferential.trim())) {
            return base;
        }
        BigDecimal value = decimal(preferential);
        return base.compareTo(value) >= 0 ? value : base;
    }

    /** "N/A" → 0, como se grababa el arancel preferencial sin preferencia. */
    static BigDecimal decimal(String value) {
        if (value == null || value.isBlank() || NOT_APPLICABLE.equals(value.trim())) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Antes CalcularCuota. PIEZA: con costo de referencia específico se toma el costo de referencia (4 decimales); si
     * no, (referencia − costo por pieza) × piezas. Otro tipo: (referencia − costo / valor UMT) × valor UMT. Si el costo
     * supera la referencia la cuota es 0.
     */
    static BigDecimal compensatoryQuota(boolean perPiece, boolean specificReference, BigDecimal referenceCost,
                                        BigDecimal unitCost, int pieces, BigDecimal tariffValue) {
        BigDecimal reference = referenceCost == null ? BigDecimal.ZERO : referenceCost;
        BigDecimal cost = unitCost == null ? BigDecimal.ZERO : unitCost;
        if (perPiece) {
            if (specificReference) {
                return reference.setScale(4, RoundingMode.DOWN);
            }
            BigDecimal count = BigDecimal.valueOf(pieces <= 0 ? 1 : pieces);
            BigDecimal costPerPiece = cost.divide(count, 10, RoundingMode.HALF_UP);
            if (costPerPiece.compareTo(reference) > 0) {
                return BigDecimal.ZERO.setScale(4);
            }
            return reference.subtract(costPerPiece).multiply(count).setScale(4, RoundingMode.HALF_UP);
        }
        if (tariffValue == null || tariffValue.signum() == 0) {
            return BigDecimal.ZERO.setScale(4);
        }
        BigDecimal costPerUnit = cost.divide(tariffValue, 10, RoundingMode.HALF_UP);
        if (costPerUnit.compareTo(reference) > 0) {
            return BigDecimal.ZERO.setScale(4);
        }
        return reference.subtract(costPerUnit).multiply(tariffValue).setScale(4, RoundingMode.HALF_UP);
    }

    /** Columna color de obtenerdatosaduanas: rango de cat_rangoporcentajes donde cae el % de permiso automático. */
    static String color(BigDecimal percentage, List<PermitColorRangeDTO> ranges) {
        BigDecimal value = percentage == null ? BigDecimal.ZERO : percentage;
        return ranges.stream()
            .filter(range -> value.compareTo(range.from()) >= 0 && value.compareTo(range.to()) <= 0)
            .map(PermitColorRangeDTO::color)
            .findFirst()
            .orElse("");
    }

    /**
     * La fracción cumple la máscara (antes {@code Contains(" ")} o largo distinto en el MaskedTextBox): {@code 0}, {@code 9}
     * y {@code #} son dígitos y lo demás se compara literal.
     */
    static boolean matchesMask(String value, String mask) {
        if (value == null || mask == null || mask.isEmpty() || value.length() != mask.length()) {
            return false;
        }
        for (int index = 0; index < mask.length(); index++) {
            char expected = mask.charAt(index);
            char actual = value.charAt(index);
            boolean digit = expected == '0' || expected == '9' || expected == '#';
            if (digit ? !Character.isDigit(actual) : actual != expected) {
                return false;
            }
        }
        return true;
    }

    /** Datos que FrmAduanas consultaba mientras se capturaba. */
    record Context(
        boolean clothing,
        boolean preferentialAvailable,
        boolean perPieceQuota
    ) {
    }

    /**
     * ValidaTodosLosCampos(TODOS) de FrmAduanas después de validar las fracciones: el primer campo que falla, en el
     * orden de la pantalla, con su mensaje.
     */
    static void validate(CustomsRowDTO row, Context context) {
        require(!blank(row.customsDescription()), AppMessages.CAPTURAR_DESCRIPCION_ADUANAL);
        // El escritorio validaba la descripción aduanal en lugar de la composición (sólo Ropa la habilita).
        require(!context.clothing() || !blank(row.composition()), AppMessages.CAPTURAR_COMPOSICION);
        require(!blank(row.englishDescription()), AppMessages.CAPTURAR_DESCRIPCION_INGLES);
        require(row.countryId() > 0, AppMessages.SELECCIONAR_PAIS);
        require(matchesMask(row.usaFraction(), USA_FRACTION_MASK), AppMessages.CAPTURAR_FRACCION_USA);
        require(positive(row.unitCost()), AppMessages.CAPTURAR_COSTO_UNITARIO);
        require(row.weightUnitId() > 0, AppMessages.SELECCIONAR_UM_PESO);
        require(positive(row.weight()), AppMessages.CAPTURAR_PESO);
        require(row.commercialUnitId() > 0, AppMessages.SELECCIONAR_UMC);
        if (row.commercialUnitId() == UMC_PIECES) {
            require(row.componentPieces() > 0, AppMessages.CAPTURAR_PIEZAS);
            require(row.componentPieces() > 1, AppMessages.PIEZAS_MAYOR_A_1);
        }
        require(positive(row.tariffValue()), AppMessages.CAPTURAR_VALOR_UMT);
        if (row.compensatoryQuota()) {
            require(row.quotaTypeId() > 0, AppMessages.SELECCIONAR_TIPO_CUOTA);
            require(positive(row.referenceCost()), AppMessages.CAPTURAR_COSTO_REFERENCIA);
        }
        if (context.preferentialAvailable() && !row.preferentialTariff()) {
            require(row.nonTariffCauseId() > 0, AppMessages.SELECCIONAR_RAZON_NO_ARANCEL);
        }
        if (row.tmec()) {
            require(row.producer() || row.producerTypeId() > 0, AppMessages.SELECCIONAR_TIPO_PRODUCTOR);
            require(row.preferentialCriterionId() > 0, AppMessages.SELECCIONAR_CRITERIO);
        }
    }

    static void require(boolean condition, String message) {
        if (!condition) {
            throw new BadRequestException(message);
        }
    }

    static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    static boolean positive(BigDecimal value) {
        return value != null && value.signum() > 0;
    }
}
