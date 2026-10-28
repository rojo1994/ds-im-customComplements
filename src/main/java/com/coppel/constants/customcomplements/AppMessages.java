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
    public static final String EMPLEADO_REQUERIDO = "No se recibió el número de empleado de la sesión.";
    public static final String SIN_IMPORTADORAS = "No se encontraron importadoras activas, favor de revisar.";
    public static final String CODIGO_NO_REGISTRADO =
        "El código capturado no se encuentra registrado en el catálogo, favor de revisar.";
    public static final String CODIGO_EN_USO = "El código capturado esta siendo utilizado en otra sesión.";
    public static final String SIN_PROVEEDOR =
        "No se encontró información del proveedor asignado al código, favor de revisar con compras.";
    public static final String SIN_MARCAS =
        "No se encontraron marcas registradas para el proveedor asignado al código, favor de revisar.";
    public static final String SIN_INVENTARIO = "El código no cuenta con información en inventario.";
    public static final String SIN_TALLAS = "No se encontraron tallas asignadas al código, favor de revisar.";
    public static final String ERROR_CARGAR_CODIGO = "Error al obtener la información del código.";

    // Guardar (ValidaTodosLosCampos TODOS, ValidaFechaVencida, ValidarCosto)
    public static final String SELECCIONAR_IMPORTADORA = "Favor de seleccionar importado por";
    public static final String SELECCIONAR_MARCA = "Favor de seleccionar marca";
    public static final String CAPTURAR_MODELO = "Favor de capturar modelo";
    public static final String FECHA_NO_VALIDA = "Favor de capturar fecha valida.";
    public static final String FECHA_VENCIDA = "Fecha vencida favor de capturar fecha valida.";
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
        + "general del código, ¿Desea continuar?";
    public static final String ERROR_GUARDAR = "Error al guardar complementos.";
    public static final String SIN_DATOS_CENTRAL = "Error al guardar información aduanal.";
    public static final String ERROR_CENTRAL_MUEBLES = "Error al guardar información aduanal en ecentral muebles.";
    public static final String ERROR_CENTRAL_ROPA = "Error al guardar información aduanal en central ropa.";

    // Información aduanal (FrmAduanas.ValidaCampo)
    public static final String SIN_MASCARA = "No se encontro configurada la máscara de la fracción, favor de revisar.";
    public static final String CAPTURAR_FRACCION_MXN = "Favor de capturar fraccion MXN.";
    public static final String FRACCION_NO_REGISTRADA =
        "La fracción capturada no se encuentra registrada o activa en el catálogo, favor de revisar.";
    public static final String CAPTURAR_DESCRIPCION_ADUANAL = "Favor de ingresar descripción aduanal para continuar.";
    public static final String CAPTURAR_COMPOSICION = "Favor de ingresar composición para continuar.";
    public static final String CAPTURAR_DESCRIPCION_INGLES = "Favor de ingresar descripción inglés para continuar.";
    public static final String CAPTURAR_FRACCION_USA = "Favor de capturar fraccion USA para continuar.";
    public static final String FRACCION_USA_NO_REGISTRADA =
        "La fracción USA capturada no se encuentra registrada en el catálogo, favor de revisar.";
    public static final String CAPTURAR_COSTO_UNITARIO = "Favor de ingresar costo unitario para continuar.";
    public static final String SELECCIONAR_UM_PESO = "Favor de seleccionar UM Peso para continuar.";
    public static final String CAPTURAR_PESO = "Favor de ingresar peso para continuar.";
    public static final String SELECCIONAR_UMC = "Favor de seleccionar UMC para continuar.";
    public static final String CAPTURAR_PIEZAS = "Favor de ingresar piezas para continuar.";
    public static final String PIEZAS_MAYOR_A_1 = "Favor de ingresar cantidad de piezas mayor a 1 para continuar.";
    public static final String CAPTURAR_VALOR_UMT = "Favor de ingresar valor UMT para continuar.";
    public static final String SELECCIONAR_TIPO_CUOTA = "Favor de seleccionar tipo de cuota para continuar.";
    public static final String CAPTURAR_COSTO_REFERENCIA = "Favor de ingresar costo de referencia para continuar.";
    public static final String SELECCIONAR_RAZON_NO_ARANCEL =
        "Favor de seleccionar razón para no aplicar arancel pref. para continuar.";
    public static final String SELECCIONAR_TIPO_PRODUCTOR = "Favor de seleccionar tipo de productor para continuar.";
    public static final String SELECCIONAR_CRITERIO = "Favor de seleccionar criterio pref para continuar.";
    public static final String CUOTA_MAXIMA = "Valor de cuota compensatoria mayor a 99999.9999";
    public static final String SELECCIONAR_PAIS = "Favor de seleccionar país origen para continuar.";

    // Causas para no aplicar arancel preferencial (FrmCausasNoAplicarAP)
    public static final String CAPTURAR_CAUSA = "Favor de ingresar una causa para no aplicar Arancel";
    public static final String CAUSA_NO_VALIDA = "La causa sólo admite letras, números y espacios.";
    public static final String CAUSA_EXISTENTE = "La causa ingresada ya existe en el catalogo";
    public static final String SELECCIONAR_TIPO_MARCA = "Favor de seleccionar el tipo de marca.";
    public static final String CAPTURAR_MARCA = "Favor de capturar la descripción de la marca.";
    public static final String MARCA_NO_VALIDA =
        "La descripción de la marca sólo admite letras, números, espacios, punto, coma y guion (máximo 50).";
    public static final String MARCA_EXISTENTE = "La marca capturada ya se encuentra registrada";
    public static final String DESCRIPCION_CAUSA_EXISTENTE = "La descripción de la causa ingresada ya existe en el catalogo";

    // Regulaciones (FrmRegulaciones.ValidarControles)
    public static final String CAPTURAR_FITOSANITARIO = "Ingresar información para certificado fitosanitario";
    public static final String FITOSANITARIO_VENCIDO = "Fecha de aviso fitosanitario vencida favor de capturar fecha valida.";
    public static final String SELECCIONAR_NOM_COMERCIAL = "Selecionar al menos una NOM de información comercial";
    public static final String SELECCIONAR_NOM_CUMPLIMIENTO = "Selecionar al menos una NOM de cumplimiento";
    public static final String CAPTURAR_FECHA_NOM = "Ingrese fecha en todos los campos";
    public static final String NOM_VENCIDA = "Fecha de Nom cumpliniento vencida favor de capturar fecha valida.";
    public static final String CAPTURAR_CERTIFICADO_O_EXCEPCION = "Favor de capturar un certificado o excepción";
    public static final String SIN_NOMS_CUMPLIMIENTO =
        "No se encontraron NOMs de cumplimiento asignadas a la fracción, favor de revisar";
    public static final String SIN_NOMS_ETIQUETADO =
        "No se encontraron NOMs de etiquetado asignadas a la fracción, favor de revisar";
    public static final String SIN_REGULACIONES =
        "La fracción asignada al componente no requiere información para la sección regulaciones.";
    public static final String SIN_IEPS = "La fracción asignada al componente no requiere información para la sección IEPS.";
    public static final String REGISTRAR_INFORMACION_ADUANAL = "Favor de registrar información aduanal para poder continuar.";

    // Aviso sanitario (FrmAvisoSanitario, MostrarMensajeAvisoSanitario)
    public static final String SELECCIONAR_ADUANA = "Favor de seleccionar aduana";
    public static final String CAPTURAR_FOLIO_O_EXCEPCION = "Favor de capturar campo folio aviso o excepción de aviso";
    public static final String SIN_EXCEPCIONES =
        "No se encontraron excepciones asignadas a la regulación, favor de verificar.";
    public static final String SIN_ADUANAS = "No se encontraron aduanas disponibles, favor de verificar.";
    public static final String AVISO_VENCIDO = "El aviso sanitario está vencido para las siguientes aduanas: %s. ";
    public static final String AVISO_POR_VENCER =
        "Está por vencer la vigencia del Aviso Sanitario para las siguientes aduanas: %s. ";

    // Especificaciones técnicas (FrmEspecificaciones)
    public static final String CAPTURAR_INFORMACION = "Favor de capturar información.";

    // IEPS (FrmIEPS)
    public static final String SELECCIONAR_CLASE = "Favor de seleccionar una Clase.";
    public static final String SELECCIONAR_CATEGORIA = "Favor de seleccionar una categoría.";
    public static final String SELECCIONAR_UNIDAD = "Favor de seleccionar una unidad de medida.";
    public static final String CAPTURAR_CANTIDAD = "Favor de capturar la cantidad.";
    public static final String CANTIDAD_ENTEROS = "No se puede capturar mas de 3 números enteros.";
    public static final String CANTIDAD_DECIMALES = "No se puede capturar mas de 3 números decimales.";

    // Rango de tallas (FrmRangoDeTallas)
    public static final String RANGO_CONSECUTIVO =
        "El rango debe ser consecutivo al número de rango establecido a la talla anterior.";
    public static final String CAPTURAR_RANGO = "Ingrese un rango de tallas para el componente.";
}
