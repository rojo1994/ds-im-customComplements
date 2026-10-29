package com.coppel.constants.customcomplements;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mensajes de negocio. Se conservan los textos de los MessageBox de MXTRF010 (FrmComplementosAduanales y sus
 * subformularios, ViewModels y DAL).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppMessages {
    public static final String ERROR_INTERNO = "Ocurrió un error interno en el servidor.";
    public static final String ERROR_PERSISTENCIA = "Error al comunicarse con el servicio de persistencia.";

    // Sesión y código (FrmComplementosAduanales.ValidaCampo TB_SKU, CargarInformacionCodigo)
    public static final String AREA_REQUERIDA = "Favor de seleccionar el área.";
    public static final String CODIGO_OBLIGATORIO = "La captura del código es obligatoria.";
    public static final String CODIGO_6_DIGITOS = "Favor de capturar el código a 6 dígitos.";
    public static final String EMPLEADO_REQUERIDO = "No se recibió el ID de empleado de la sesión.";
    public static final String SIN_IMPORTADORAS = "No se encontraron importadoras activas, favor de revisar.";
    public static final String CODIGO_NO_REGISTRADO =
        "El código capturado no se encuentra registrado en el catálogo, favor de revisar.";
    public static final String CODIGO_EN_USO = "El código capturado está siendo utilizado en otra sesión.";
    public static final String SIN_PROVEEDOR =
        "No se encontró información del proveedor asignado al código, favor de revisar con compras.";
    public static final String SIN_MARCAS =
        "No se encontraron marcas registradas para el proveedor asignado al código, favor de revisar.";
    public static final String SIN_INVENTARIO = "El código no cuenta con información en inventario.";
    public static final String SIN_TALLAS = "No se encontraron tallas asignadas al código, favor de revisar.";
    public static final String ERROR_CARGAR_CODIGO = "Error al obtener la información del código.";

    // Guardar (ValidaTodosLosCampos TODOS, ValidaFechaVencida, ValidarCosto)
    public static final String SELECCIONAR_IMPORTADORA = "Favor de seleccionar la importadora.";
    public static final String SELECCIONAR_MARCA = "Favor de seleccionar la marca.";
    public static final String CAPTURAR_MODELO = "Favor de capturar el modelo.";
    public static final String FECHA_NO_VALIDA = "Favor de capturar fecha válida.";
    public static final String FECHA_VENCIDA = "Fecha vencida, favor de capturar una fecha válida.";
    public static final String CAPTURAR_COMPONENTES = "Favor de capturar número de componentes.";
    public static final String COMPONENTES_MAYOR_A_1 = "Favor de capturar número de componentes mayor a 1.";
    public static final String COMPONENTES_MENOR =
        "El número de componentes es menor al mostrado en componentes, puede borrar componentes en caso de no ser requeridos.";
    public static final String CAPTURAR_DESCRIPCION_GENERAL = "Favor de capturar descripción general.";
    public static final String INFORMACION_ADUANAL_COMPONENTES =
        "Favor de capturar información aduanal de todos los componentes.";
    public static final String SELECCIONAR_COMPONENTE_PRINCIPAL = "Favor de seleccionar componente principal.";
    public static final String VERIFICAR_COSTOS = "Favor de verificar los datos de costos.";
    public static final String DIFERENCIA_COSTOS = "Existe diferencia entre la suma de los costos capturados y el costo "
        + "general del código, ¿desea continuar?";
    public static final String ERROR_GUARDAR = "Error al guardar complementos.";
    public static final String SIN_DATOS_CENTRAL = "Error al guardar información aduanal.";
    public static final String ERROR_CENTRAL_MUEBLES = "Error al guardar información aduanal en la central de muebles.";
    public static final String ERROR_CENTRAL_ROPA = "Error al guardar información aduanal en la central de ropa.";

    // Información aduanal (FrmAduanas.ValidaCampo)
    public static final String SIN_MASCARA = "No se encontró configurada la máscara de la fracción, favor de revisar.";
    public static final String CAPTURAR_FRACCION_MXN = "Favor de capturar fracción MXN.";
    public static final String FRACCION_NO_REGISTRADA =
        "La fracción capturada no se encuentra registrada o activa en el catálogo, favor de revisar.";
    public static final String CAPTURAR_DESCRIPCION_ADUANAL = "Favor de ingresar descripción aduanal para continuar.";
    public static final String CAPTURAR_COMPOSICION = "Favor de ingresar composición para continuar.";
    public static final String CAPTURAR_DESCRIPCION_INGLES = "Favor de ingresar descripción en inglés para continuar.";
    public static final String CAPTURAR_FRACCION_USA = "Favor de capturar fracción USA para continuar.";
    public static final String FRACCION_USA_NO_REGISTRADA =
        "La fracción USA capturada no se encuentra registrada en el catálogo, favor de revisar.";
    public static final String CAPTURAR_COSTO_UNITARIO = "Favor de ingresar costo unitario para continuar.";
    public static final String SELECCIONAR_UM_PESO = "Favor de seleccionar unidad de medida de peso para continuar.";
    public static final String CAPTURAR_PESO = "Favor de ingresar peso para continuar.";
    public static final String SELECCIONAR_UMC = "Favor de seleccionar unidad de medida comercial para continuar.";
    public static final String CAPTURAR_PIEZAS = "Favor de ingresar piezas para continuar.";
    public static final String PIEZAS_MAYOR_A_1 = "Favor de ingresar cantidad de piezas mayor a 1 para continuar.";
    public static final String CAPTURAR_VALOR_UMT = "Favor de ingresar valor de la unidad de medida de tarifa para continuar.";
    public static final String SELECCIONAR_TIPO_CUOTA = "Favor de seleccionar tipo de cuota para continuar.";
    public static final String CAPTURAR_COSTO_REFERENCIA = "Favor de ingresar costo de referencia para continuar.";
    public static final String SELECCIONAR_RAZON_NO_ARANCEL =
        "Favor de seleccionar razón para no aplicar arancel preferencial para continuar.";
    public static final String SELECCIONAR_TIPO_PRODUCTOR = "Favor de seleccionar tipo de productor para continuar.";
    public static final String SELECCIONAR_CRITERIO = "Favor de seleccionar criterio preferencial para continuar.";
    public static final String CUOTA_MAXIMA = "El valor de la cuota compensatoria es mayor a 99999.9999.";
    public static final String SELECCIONAR_PAIS = "Favor de seleccionar país de origen para continuar.";

    // Causas para no aplicar arancel preferencial (FrmCausasNoAplicarAP)
    public static final String CAPTURAR_CAUSA = "Favor de ingresar una causa para no aplicar arancel.";
    public static final String CAUSA_NO_VALIDA = "La causa sólo admite letras, números y espacios.";
    public static final String CAUSA_EXISTENTE = "La causa ingresada ya existe en el catálogo.";
    public static final String SELECCIONAR_TIPO_MARCA = "Favor de seleccionar el tipo de marca.";
    public static final String CAPTURAR_MARCA = "Favor de capturar la descripción de la marca.";
    public static final String MARCA_NO_VALIDA =
        "La descripción de la marca sólo admite letras, números, espacios, punto, coma y guion (máximo 50).";
    public static final String MARCA_EXISTENTE = "La marca capturada ya se encuentra registrada.";
    public static final String DESCRIPCION_CAUSA_EXISTENTE = "La descripción de la causa ingresada ya existe en el catálogo.";

    // Regulaciones (FrmRegulaciones.ValidarControles)
    public static final String CAPTURAR_FITOSANITARIO = "Favor de ingresar información del certificado fitosanitario.";
    public static final String FITOSANITARIO_VENCIDO = "La fecha del certificado fitosanitario está vencida, favor de capturar una fecha válida.";
    public static final String SELECCIONAR_NOM_COMERCIAL = "Favor de seleccionar al menos una NOM de información comercial.";
    public static final String SELECCIONAR_NOM_CUMPLIMIENTO = "Favor de seleccionar al menos una NOM de cumplimiento.";
    public static final String CAPTURAR_FECHA_NOM = "Favor de capturar la fecha en todos los campos.";
    public static final String NOM_VENCIDA = "La fecha de la NOM de cumplimiento está vencida, favor de capturar una fecha válida.";
    public static final String CAPTURAR_CERTIFICADO_O_EXCEPCION = "Favor de capturar un certificado o excepción.";
    public static final String SIN_NOMS_CUMPLIMIENTO =
        "No se encontraron NOM de cumplimiento asignadas a la fracción, favor de revisar.";
    public static final String SIN_NOMS_ETIQUETADO =
        "No se encontraron NOM de etiquetado asignadas a la fracción, favor de revisar.";
    public static final String SIN_REGULACIONES =
        "La fracción asignada al componente no requiere información para la sección regulaciones.";
    public static final String SIN_IEPS = "La fracción asignada al componente no requiere información para la sección IEPS.";
    public static final String REGISTRAR_INFORMACION_ADUANAL = "Favor de registrar información aduanal para poder continuar.";

    // Aviso sanitario (FrmAvisoSanitario, MostrarMensajeAvisoSanitario)
    public static final String SELECCIONAR_ADUANA = "Favor de seleccionar aduana.";
    public static final String CAPTURAR_FOLIO_O_EXCEPCION = "Favor de capturar el folio del aviso o la excepción de aviso.";
    public static final String SIN_EXCEPCIONES =
        "No se encontraron excepciones asignadas a la regulación, favor de verificar.";
    public static final String SIN_ADUANAS = "No se encontraron aduanas disponibles, favor de verificar.";
    public static final String AVISO_VENCIDO = "El aviso sanitario está vencido para las siguientes aduanas: %s. ";
    public static final String AVISO_POR_VENCER =
        "Está por vencer la vigencia del aviso sanitario para las siguientes aduanas: %s. ";

    // Especificaciones técnicas (FrmEspecificaciones)
    public static final String CAPTURAR_INFORMACION = "Favor de capturar información.";

    // IEPS (FrmIEPS)
    public static final String SELECCIONAR_CLASE = "Favor de seleccionar una clase.";
    public static final String SELECCIONAR_CATEGORIA = "Favor de seleccionar una categoría.";
    public static final String SELECCIONAR_UNIDAD = "Favor de seleccionar una unidad de medida.";
    public static final String CAPTURAR_CANTIDAD = "Favor de capturar la cantidad.";
    public static final String CANTIDAD_ENTEROS = "No se puede capturar más de 3 números enteros.";
    public static final String CANTIDAD_DECIMALES = "No se puede capturar más de 3 números decimales.";

    // Rango de tallas (FrmRangoDeTallas)
    public static final String RANGO_CONSECUTIVO =
        "El rango debe ser consecutivo al número de rango establecido a la talla anterior.";
    public static final String CAPTURAR_RANGO = "Favor de capturar un rango de tallas para el componente.";
}
