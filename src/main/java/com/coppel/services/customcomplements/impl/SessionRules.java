package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.CodeGeneralDTO;
import com.coppel.client.customcomplements.dto.InventoryRowDTO;
import com.coppel.client.customcomplements.dto.SizeLimitsDTO;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import com.coppel.dto.response.customcomplements.InventoryDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reglas de la carga del código (antes CargarInformacionCodigo y ComplementosAduanalesDAL).
 */
final class SessionRules {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private SessionRules() {
    }

    /**
     * Antes ObtenerInventario: Muebles trae CostoDolares en centavos (se divide entre 100 si tiene 3 dígitos o más);
     * Ropa trae el importe total y el costo es importe / existencia truncado a 2 decimales.
     */
    static InventoryDTO inventory(CustomsArea area, InventoryRowDTO row) {
        BigDecimal amount = row.amount() == null ? BigDecimal.ZERO : row.amount();
        BigDecimal cost;
        if (area.isClothing()) {
            cost = row.quantity() > 0
                ? amount.divide(BigDecimal.valueOf(row.quantity()), 2, RoundingMode.DOWN)
                : BigDecimal.ZERO.setScale(2);
        } else {
            cost = amount.abs().toBigInteger().toString().length() >= 3
                ? amount.divide(HUNDRED, 2, RoundingMode.HALF_UP)
                : amount.setScale(2, RoundingMode.HALF_UP);
        }
        return new InventoryDTO(row.warehouse(), row.quantity(), cost);
    }

    /** Antes CostoGeneral: Muebles CostoInteriorDL / 100; Ropa el CostoUnitario tal cual. */
    static BigDecimal generalCost(CustomsArea area, BigDecimal interiorCost) {
        BigDecimal value = interiorCost == null ? BigDecimal.ZERO : interiorCost;
        return area.isClothing() ? value.setScale(2, RoundingMode.HALF_UP) : value.divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    /**
     * Marca del combo: la capturada; si el código es nuevo, la que coincide con la marca de compras (el combo estaba
     * enlazado al texto de la marca) o la primera.
     */
    static Optional<Integer> defaultBrand(CodeGeneralDTO general, List<CatalogItemDTO> brands) {
        if (general.brandId() != null && general.brandId() > 0) {
            return Optional.of(general.brandId());
        }
        return brands.stream()
            .filter(brand -> brand.name().equalsIgnoreCase(general.purchaseBrand().trim()))
            .findFirst()
            .or(() -> brands.stream().findFirst())
            .map(brand -> Integer.parseInt(brand.id()));
    }

    /** "mín-máx" de las tallas del artículo (antes ObtenerTallas); vacío si no tiene. */
    static String articleSizes(SizeLimitsDTO limits) {
        if (limits == null || limits.minSize() == null || limits.maxSize() == null) {
            return "";
        }
        return limits.minSize() + "-" + limits.maxSize();
    }

    /** Pieza / rango representativos ya resueltos. */
    record Representative(int piece, int range) {
    }

    /** Grid de componentes ya resuelto con el representativo que quedó. */
    record Components(List<ComponentDTO> rows, Representative representative) {
    }

    /**
     * Antes ObtenerDatosAduanas + el armado de CargarInformacionCodigo: un renglón por pieza / rango guardado; si no
     * hay información aduanal, la pieza 1 (en Ropa con todas las tallas en el rango 1). Si ningún renglón es el
     * representativo guardado, lo es el primero.
     */
    static Components components(CustomsArea area, List<CustomsRowDTO> customs, List<SizeRowDTO> sizes,
                                 int representativePiece, int representativeRange, String articleSizes) {
        List<ComponentDTO> rows = new ArrayList<>();
        if (customs.isEmpty()) {
            int range = area.isClothing() ? 1 : 0;
            rows.add(new ComponentDTO(1, range, area.isClothing() ? articleSizes : "", true));
            return new Components(rows, new Representative(1, range));
        }
        for (CustomsRowDTO row : customs) {
            String text = area.isClothing() ? sizesOf(sizes, row.piece(), row.sizeRange()) : "";
            rows.add(new ComponentDTO(row.piece(), row.sizeRange(), text,
                row.piece() == representativePiece && row.sizeRange() == representativeRange));
        }
        if (rows.stream().noneMatch(ComponentDTO::representative)) {
            ComponentDTO first = rows.getFirst();
            rows.set(0, new ComponentDTO(first.piece(), first.sizeRange(), first.sizes(), true));
            return new Components(rows, new Representative(first.piece(), first.sizeRange()));
        }
        return new Components(rows, new Representative(representativePiece, representativeRange));
    }

    /** Número de componentes del código: piezas distintas (Ropa) o renglones (Muebles). */
    static int componentCount(CustomsArea area, List<ComponentDTO> components) {
        return area.isClothing()
            ? (int) components.stream().mapToInt(ComponentDTO::piece).distinct().count()
            : components.size();
    }

    private static String sizesOf(List<SizeRowDTO> sizes, int piece, int range) {
        List<SizeRowDTO> group = sizes.stream()
            .filter(size -> size.piece() == piece && size.range() == range)
            .toList();
        return group.isEmpty() ? "" : DetailRules.sizesText(group);
    }
}
