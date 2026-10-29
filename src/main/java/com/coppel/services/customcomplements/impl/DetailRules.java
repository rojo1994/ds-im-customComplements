package com.coppel.services.customcomplements.impl;

import com.coppel.client.customcomplements.dto.IepsCategoryDTO;
import com.coppel.client.customcomplements.dto.IepsUnitDTO;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.ExpirationDocument;
import com.coppel.constants.customcomplements.ExpirationStatus;
import com.coppel.dto.customcomplements.ElectricalDTO;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;
import com.coppel.dto.request.customcomplements.IepsRequest;
import com.coppel.dto.request.customcomplements.NomInput;
import com.coppel.dto.request.customcomplements.RegulationsRequest;
import com.coppel.dto.request.customcomplements.SanitaryNoticeRequest;
import com.coppel.dto.request.customcomplements.SanitaryOfficeInput;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import com.coppel.dto.response.customcomplements.ExpirationStatusDTO;
import com.coppel.dto.response.customcomplements.SanitaryNoticeResultDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.coppel.services.customcomplements.impl.CustomsRules.blank;
import static com.coppel.services.customcomplements.impl.CustomsRules.require;

/**
 * Reglas de los subformularios por pieza (FrmRegulaciones, FrmAvisoSanitario, FrmEspecificaciones, FrmIEPS y
 * FrmRangoDeTallas). Las consultas a la base llegan como parámetros (aplicabilidad, catálogos, vencimientos).
 */
final class DetailRules {

    static final LocalDate EMPTY_DATE = ExpirationChecker.EMPTY_DATE;
    static final int PHYTOSANITARY = 2;
    static final int PROFEPA = 3;
    static final int COMPLIANCE_NOM = 4;
    static final int LABELING_NOM = 5;
    static final int SANITARY_NOTICE = 1;
    /** Cantidad de consumo energético vacía como la graba el escritorio. */
    static final String EMPTY_QUANTITY = "0,0000";

    private DetailRules() {
    }

    /** Qué secciones de FrmRegulaciones lleva la fracción (validarregulacion 2, 3, 4 y 5). */
    record Applicability(boolean phytosanitary, boolean profepa, boolean complianceNoms, boolean labelingNoms) {
    }

    // ------------------------------------------------------------------ Regulaciones

    /**
     * Antes ValidarControles + LlenarTemporales de FrmRegulaciones: valida en el mismo orden y regresa los renglones
     * de las regulaciones 2..5 de la pieza / rango.
     */
    static List<RegulationRowDTO> regulations(RegulationsRequest request, Applicability applies,
                                              BiFunction<LocalDate, ExpirationDocument, ExpirationStatusDTO> expiration) {
        if (applies.phytosanitary()) {
            boolean hasException = positive(request.phytosanitaryException());
            require(hasException || !blank(request.phytosanitaryCertificate()), AppMessages.CAPTURAR_FITOSANITARIO);
            if (!blank(request.phytosanitaryCertificate())) {
                require(expiration.apply(request.phytosanitaryValidity(), ExpirationDocument.PHYTOSANITARY).status()
                    != ExpirationStatus.EXPIRED, AppMessages.FITOSANITARIO_VENCIDO);
            }
        }
        if (applies.labelingNoms()) {
            require(request.labelingNoms().stream().anyMatch(NomInput::applies), AppMessages.SELECCIONAR_NOM_COMERCIAL);
        }
        if (applies.complianceNoms() && !request.complianceInProcess()) {
            List<NomInput> selected = request.complianceNoms().stream().filter(NomInput::applies).toList();
            require(!selected.isEmpty(), AppMessages.SELECCIONAR_NOM_CUMPLIMIENTO);
            for (NomInput nom : selected) {
                if (!blank(nom.certificate())) {
                    require(nom.validity() != null && nom.validity().isAfter(EMPTY_DATE), AppMessages.CAPTURAR_FECHA_NOM);
                    require(expiration.apply(nom.validity(), ExpirationDocument.NOM).status() != ExpirationStatus.EXPIRED,
                        AppMessages.NOM_VENCIDA);
                } else {
                    require(positive(nom.exceptionId()), AppMessages.CAPTURAR_CERTIFICADO_O_EXCEPCION);
                }
            }
        }

        int piece = request.piece();
        int range = request.sizeRange();
        String fraction = request.fraction().trim();
        List<RegulationRowDTO> rows = new ArrayList<>();
        if (applies.profepa()) {
            rows.add(row(piece, range, fraction, PROFEPA, 0, "", EMPTY_DATE, value(request.profepaException()), false));
        }
        if (applies.phytosanitary()
            && (!blank(request.phytosanitaryCertificate()) || positive(request.phytosanitaryException()))) {
            boolean withCertificate = !blank(request.phytosanitaryCertificate());
            rows.add(row(piece, range, fraction, PHYTOSANITARY, 0, text(request.phytosanitaryCertificate()),
                withCertificate ? date(request.phytosanitaryValidity()) : EMPTY_DATE,
                withCertificate ? 0 : value(request.phytosanitaryException()), false));
        }
        if (applies.labelingNoms()) {
            request.labelingNoms().stream().filter(NomInput::applies).forEach(nom -> rows.add(
                row(piece, range, fraction, LABELING_NOM, nom.nomId(), "", EMPTY_DATE, value(nom.exceptionId()), false)));
        }
        if (applies.complianceNoms()) {
            if (request.complianceInProcess()) {
                // Antes RegulacionesDAL: NOM 0 con la regulación 4 se graba con opc_nomtramite = 1.
                rows.add(row(piece, range, fraction, COMPLIANCE_NOM, 0, "", EMPTY_DATE, 0, true));
            }
            request.complianceNoms().stream().filter(NomInput::applies).forEach(nom -> {
                boolean withCertificate = !blank(nom.certificate());
                rows.add(row(piece, range, fraction, COMPLIANCE_NOM, nom.nomId(), text(nom.certificate()),
                    withCertificate ? date(nom.validity()) : EMPTY_DATE, withCertificate ? 0 : value(nom.exceptionId()),
                    false));
            });
        }
        return rows;
    }

    // ------------------------------------------------------------------ Aviso sanitario

    /**
     * Antes FrmAvisoSanitario.Guardar + ActualizarRegulaciones + MostrarMensajeAvisoSanitario: al menos una aduana,
     * folio o excepción en cada aduana marcada y el resumen de avisos vencidos / por vencer.
     */
    static SanitaryNoticeResultDTO sanitaryNotice(SanitaryNoticeRequest request,
                                                  BiFunction<LocalDate, ExpirationDocument, ExpirationStatusDTO> expiration) {
        List<SanitaryOfficeInput> selected = request.offices().stream().filter(SanitaryOfficeInput::applies).toList();
        require(!selected.isEmpty(), AppMessages.SELECCIONAR_ADUANA);
        for (SanitaryOfficeInput office : selected) {
            require(positive(office.exceptionId()) || !blank(office.folio()), AppMessages.CAPTURAR_FOLIO_O_EXCEPCION);
        }

        List<RegulationRowDTO> rows = new ArrayList<>();
        List<String> expired = new ArrayList<>();
        List<String> expiring = new ArrayList<>();
        for (SanitaryOfficeInput office : selected) {
            boolean withFolio = !blank(office.folio());
            LocalDate validity = withFolio ? date(office.validity()) : EMPTY_DATE;
            rows.add(row(request.representativePiece(), request.representativeRange(), text(request.fraction()),
                SANITARY_NOTICE, office.officeId(), withFolio ? office.folio().trim() : "", validity,
                withFolio ? 0 : value(office.exceptionId()), false));
            if (withFolio) {
                ExpirationStatusDTO status = expiration.apply(validity, ExpirationDocument.SANITARY_NOTICE);
                if (status.status() == ExpirationStatus.EXPIRED) {
                    expired.add(office.office());
                } else if (status.status() == ExpirationStatus.EXPIRING) {
                    expiring.add(office.office() + " - " + remainingDays(status.message()));
                }
            }
        }
        return new SanitaryNoticeResultDTO(rows, expirationSummary(expired, expiring));
    }

    /** Texto de MostrarMensajeAvisoSanitario (vencidos, luego por vencer en otro párrafo). */
    static String expirationSummary(List<String> expired, List<String> expiring) {
        StringBuilder message = new StringBuilder();
        if (!expired.isEmpty()) {
            message.append(String.format(AppMessages.AVISO_VENCIDO, String.join(", ", expired)));
        }
        if (!expiring.isEmpty()) {
            if (!message.isEmpty()) {
                message.append(System.lineSeparator()).append(System.lineSeparator());
            }
            message.append(String.format(AppMessages.AVISO_POR_VENCER, String.join(", ", expiring)));
        }
        return message.toString().trim();
    }

    /** "Está por vencer la vigencia de X. Quedan 5 días" → "5 días" (antes Split('.') + Replace(" Quedan ")). */
    static String remainingDays(String message) {
        if (message == null) {
            return "";
        }
        String[] parts = message.split("\\.");
        return parts[parts.length - 1].replace(" Quedan ", "").trim();
    }

    // ------------------------------------------------------------------ Especificaciones técnicas

    /**
     * Antes btnGrabar_Click + ValidaCampo de FrmEspecificaciones: debe tener componentes eléctricos o consumo
     * energético; el adaptador pide sus datos de entrada y salida, el control remoto su corriente y pilas y el
     * consumo energético cada unidad con su cantidad. Lo que no aplica se limpia como al desmarcar las casillas.
     */
    static SpecificationRowDTO specifications(SpecificationRowDTO row) {
        require(row.hasComponents() || row.hasEnergyConsumption(), AppMessages.CAPTURAR_INFORMACION);
        ElectricalDTO adapterIn = row.hasComponents() && row.hasAdapter() ? row.adapterInput() : ElectricalDTO.EMPTY;
        ElectricalDTO adapterOut = row.hasComponents() && row.hasAdapter() ? row.adapterOutput() : ElectricalDTO.EMPTY;
        ElectricalDTO remote = row.hasComponents() && row.hasRemoteControl() ? row.remoteControl() : ElectricalDTO.EMPTY;
        if (row.hasComponents() && row.hasAdapter()) {
            require(!blank(adapterIn.volts()), "Favor de capturar el voltaje de entrada del adaptador.");
            require(!blank(adapterOut.volts()), "Favor de capturar el voltaje de salida del adaptador.");
            require(!blank(adapterIn.watts()), "Favor de capturar los watts de entrada del adaptador.");
            require(!blank(adapterOut.watts()), "Favor de capturar los watts de salida del adaptador.");
            require(!blank(adapterIn.hertz()), "Favor de capturar los hertz de entrada del adaptador.");
            require(!blank(adapterOut.hertz()), "Favor de capturar los hertz de salida del adaptador.");
            require(!blank(adapterIn.amperes()), "Favor de capturar los amperes de entrada del adaptador.");
            require(!blank(adapterOut.amperes()), "Favor de capturar los amperes de salida del adaptador.");
            require(!blank(adapterIn.megaAmperes()), "Favor de capturar los megaamperes de entrada del adaptador.");
            require(!blank(adapterOut.megaAmperes()), "Favor de capturar los megaamperes de salida del adaptador.");
            require(!blank(adapterIn.directCurrent()), "Favor de capturar la corriente directa de entrada del adaptador.");
            require(!blank(adapterOut.directCurrent()), "Favor de capturar la corriente directa de salida del adaptador.");
        }
        if (row.hasComponents() && row.hasRemoteControl()) {
            require(!blank(remote.directCurrent()), "Favor de capturar la corriente directa del control remoto.");
            require(!blank(remote.batteries()), "Favor de capturar la cantidad de pilas del control remoto.");
            require(!blank(remote.batteryType()), "Favor de capturar el tipo de pilas del control remoto.");
            require(!blank(remote.batteryVoltage()), "Favor de capturar el voltaje de pilas del control remoto.");
        }
        if (row.hasEnergyConsumption()) {
            require(row.consumptionUnitId() > 0, "Favor de seleccionar la unidad del consumo energético.");
            require(!emptyQuantity(row.consumptionQuantity()), "Favor de capturar la cantidad del consumo energético.");
            require(row.standbyUnitId() > 0, "Favor de seleccionar la unidad del consumo energético en espera.");
            require(!emptyQuantity(row.standbyQuantity()),
                "Favor de capturar la cantidad del consumo energético en espera.");
            require(row.consumedUnitId() > 0, "Favor de seleccionar la unidad de energía consumida.");
            require(!emptyQuantity(row.consumedQuantity()), "Favor de capturar la cantidad de energía consumida.");
        }
        ElectricalDTO product = row.hasComponents() ? productWithBatteries(row.product()) : ElectricalDTO.EMPTY;
        return new SpecificationRowDTO(row.piece(), row.sizeRange(), row.hasComponents(),
            row.hasComponents() && row.under24Volts(), row.hasComponents() && row.hasAdapter(),
            row.hasComponents() && row.hasRemoteControl(), row.hasEnergyConsumption(),
            row.hasEnergyConsumption() ? row.consumptionUnitId() : 0,
            row.hasEnergyConsumption() ? text(row.consumptionQuantity()) : EMPTY_QUANTITY,
            row.hasEnergyConsumption() ? row.standbyUnitId() : 0,
            row.hasEnergyConsumption() ? text(row.standbyQuantity()) : EMPTY_QUANTITY,
            row.hasEnergyConsumption() ? row.consumedUnitId() : 0,
            row.hasEnergyConsumption() ? text(row.consumedQuantity()) : EMPTY_QUANTITY,
            product, trimmed(adapterIn), trimmed(adapterOut), trimmed(remote));
    }

    /** Sin corriente directa del producto no hay pilas (antes tbCDProducto_TextChanged). */
    private static ElectricalDTO productWithBatteries(ElectricalDTO product) {
        ElectricalDTO value = trimmed(product);
        if (blank(value.directCurrent())) {
            return new ElectricalDTO(value.volts(), value.watts(), value.hertz(), value.amperes(), value.megaAmperes(),
                "", "", "", "");
        }
        return value;
    }

    /** Una unidad "N/A" deja la cantidad en 0,0000 y deshabilitada: no se exige. */
    private static boolean emptyQuantity(String quantity) {
        return blank(quantity);
    }

    private static ElectricalDTO trimmed(ElectricalDTO value) {
        ElectricalDTO data = value == null ? ElectricalDTO.EMPTY : value;
        return new ElectricalDTO(text(data.volts()), text(data.watts()), text(data.hertz()), text(data.amperes()),
            text(data.megaAmperes()), text(data.directCurrent()), text(data.batteries()), text(data.batteryType()),
            text(data.batteryVoltage()));
    }

    // ------------------------------------------------------------------ IEPS

    /**
     * Antes ValidaTodosLosCampos(CB_UMIEPS) + ABCIEPS de FrmIEPS: clase, categoría y unidad obligatorias; la cantidad
     * con 3 enteros y 3 decimales (sin decimales si la unidad no los admite). Porcentaje y costo adicional salen de la
     * categoría.
     */
    static IepsRowDTO ieps(IepsRequest request, List<IepsCategoryDTO> categories, List<IepsUnitDTO> units) {
        require(positive(request.classId()), AppMessages.SELECCIONAR_CLASE);
        IepsCategoryDTO category = categories.stream()
            .filter(item -> request.categoryId() != null && item.id() == request.categoryId())
            .findFirst()
            .orElse(null);
        require(category != null, AppMessages.SELECCIONAR_CATEGORIA);
        IepsUnitDTO unit = units.stream()
            .filter(item -> request.unitId() != null && item.id() == request.unitId())
            .findFirst()
            .orElse(null);
        require(unit != null, AppMessages.SELECCIONAR_UNIDAD);
        BigDecimal quantity = request.quantity() == null ? BigDecimal.ZERO : request.quantity();
        BigDecimal scaled = unit.decimals() ? quantity : quantity.setScale(0, RoundingMode.DOWN);
        require(scaled.abs().compareTo(new BigDecimal("1000")) < 0, AppMessages.CANTIDAD_ENTEROS);
        require(scaled.stripTrailingZeros().scale() <= 3, AppMessages.CANTIDAD_DECIMALES);
        return new IepsRowDTO(request.piece(), request.sizeRange(), request.classId(), category.id(), category.percentage(),
            category.additionalCost(), unit.id(), scaled.setScale(3, RoundingMode.DOWN));
    }

    // ------------------------------------------------------------------ Rango de tallas

    /**
     * Antes dgvRangoDeTallas_CellValidating: en cada pieza la primera talla es el rango 1 y cada talla siguiente
     * conserva el rango de la anterior o el siguiente.
     */
    static List<SizeRowDTO> sizeRanges(List<SizeRowDTO> sizes) {
        Map<Integer, List<SizeRowDTO>> byPiece = sizes.stream()
            .sorted(Comparator.comparingInt(SizeRowDTO::piece).thenComparingInt(SizeRowDTO::size))
            .collect(Collectors.groupingBy(SizeRowDTO::piece, LinkedHashMap::new, Collectors.toList()));
        List<SizeRowDTO> result = new ArrayList<>();
        for (List<SizeRowDTO> pieceSizes : byPiece.values()) {
            int previous = 0;
            for (int index = 0; index < pieceSizes.size(); index++) {
                SizeRowDTO size = pieceSizes.get(index);
                require(size.range() > 0, AppMessages.CAPTURAR_RANGO);
                int range = index == 0 ? 1 : size.range();
                require(range == previous || range == previous + 1, AppMessages.RANGO_CONSECUTIVO);
                result.add(new SizeRowDTO(size.piece(), size.size(), range));
                previous = range;
            }
        }
        return result;
    }

    /**
     * Antes RangoTallas de FrmComplementosAduanales: un renglón del grid por pieza / rango con su texto de tallas; el
     * representativo se conserva si la pieza / rango sigue existiendo.
     */
    static List<ComponentDTO> componentsFromSizes(List<SizeRowDTO> sizes, int representativePiece,
                                                  int representativeRange) {
        Map<String, List<SizeRowDTO>> groups = sizes.stream()
            .sorted(Comparator.comparingInt(SizeRowDTO::piece).thenComparingInt(SizeRowDTO::range)
                .thenComparingInt(SizeRowDTO::size))
            .collect(Collectors.groupingBy(size -> size.piece() + "|" + size.range(), LinkedHashMap::new,
                Collectors.toList()));
        List<ComponentDTO> components = new ArrayList<>();
        for (List<SizeRowDTO> group : groups.values()) {
            SizeRowDTO first = group.getFirst();
            components.add(new ComponentDTO(first.piece(), first.range(), sizesText(group),
                first.piece() == representativePiece && first.range() == representativeRange));
        }
        return components;
    }

    /** "mín-máx" o una sola talla (antes ObtenerDatosRangoTallas opción 0). */
    static String sizesText(List<SizeRowDTO> group) {
        int min = group.stream().mapToInt(SizeRowDTO::size).min().orElse(0);
        int max = group.stream().mapToInt(SizeRowDTO::size).max().orElse(0);
        return min == max ? String.valueOf(min) : min + "-" + max;
    }

    // ------------------------------------------------------------------ Utilidades

    private static RegulationRowDTO row(int piece, int range, String fraction, int regulation, int nomOrOffice,
                                        String certificate, LocalDate validity, int exceptionId, boolean inProcess) {
        return new RegulationRowDTO(piece, range, fraction, regulation, nomOrOffice, certificate, validity, exceptionId,
            inProcess);
    }

    private static boolean positive(Integer value) {
        return value != null && value > 0;
    }

    private static int value(Integer value) {
        return value == null || value < 0 ? 0 : value;
    }

    private static LocalDate date(LocalDate value) {
        return value == null ? EMPTY_DATE : value;
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }
}
