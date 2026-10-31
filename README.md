# ds-im-customComplements

API de **negocio** de Complementos aduana (migración de `MxTrf010-complementos-aduana`, `MXTRF010.DLL`). Contiene
las reglas que vivían en `FrmComplementosAduanales`, sus subformularios, los ViewModels y los DAL, con los mismos
mensajes de los `MessageBox`, y persiste a través de `ps-im-customComplements`; la copia a Central Muebles / Central
Ropa la graba con `ps-tms-fmd-inventarios-centrales` (reenvía el header `Authorization`). Lo consume
`im-spa-custom-complements`.

Java 25 · Spring Boot 4.1.1 · `spring-boot-starter-restclient` (RestClient + interfaz `@HttpExchange`) · springdoc 3 ·
todos los DTO son `record`. Base común de `ps-im-CustomsDocumentsM3`: CORS, `WebSecurityConfig`,
`ApiResponseDTO { meta, data }`, `LogUtil`, manejador de errores y aspecto de bitácora.

## Estructura

```text
com.coppel
├── controller/customcomplements     Catalog · Session (abrir / guardar / soltar) · CustomsInfo · Details
├── services/customcomplements       interfaces + impl
│   └── impl                         CustomsRules · DetailRules · SessionRules · SaveRules (reglas puras, con pruebas)
│                                    FractionRulesResolver · ExpirationChecker
├── client/customcomplements         CustomComplementsPsClient e InventoryCentersClient (@HttpExchange),
│                                    PsClientErrorHandler, records de ps
├── dto
│   ├── customcomplements            renglones de aduanas, tallas, regulaciones, especificaciones e IEPS (front ↔ ds ↔ ps)
│   ├── request/customcomplements    records de entrada (validados)
│   └── response/customcomplements   records de salida
├── config                           JacksonConfig, PsClientConfig, InventoryCentersClientConfig, propiedades
├── constants/customcomplements      AppMessages (textos del escritorio), CustomsArea, ExpirationDocument, …
├── exceptions/customcomplements     ComplementsExceptionHandler + excepciones (400, 404, 409, 502)
└── aspects/customcomplements        ComplementsLoggingAspect
```

## Endpoints

Base `<context-path>/v1/custom-complements` · Swagger en `<context-path>/swagger-ui.html`.

| Método | Ruta | Antes en MXTRF010 |
|---|---|---|
| GET | `/catalogs` | `FrmComplementosAduanales_Load` (importadoras) + los combos que cada subformulario cargaba al abrirse |
| GET | `/catalogs/ieps-categories?classId=` · `/catalogs/exceptions?regulation=` · `/catalogs/noms?fraction=&regulation=` | combos dependientes |
| POST | `/sessions/open` | `CargarInformacionCodigo` (validar, ¿en uso?, proveedor, marcas, inventario, datos, temporales, tomar el código) |
| POST | `/sessions/close` | `[F12] Limpiar` / Salir (`VerificarCodigoEnUso(3)`) |
| POST | `/brands/change` | `ValidaImpi` + `ObtenerTipoMarca` |
| POST | `/brands` | Alta de marca (del front anterior; no existía en el escritorio): el proveedor sale del código, 409 si ya existe; regresa el combo actualizado |
| POST | `/complements/save` | `[F10] Guardar`: validaciones, costos, `grabarcomplementos`, IMPI, servidor central, soltar el código |
| POST | `/customs-info/fraction` · GET `/customs-info/tariffs` · GET `/customs-info/usa-fraction` · POST `/customs-info/automatic-permit` · POST `/customs-info/accept` | `FrmAduanas` |
| GET / POST / PUT / PATCH | `/non-tariff-causes` … | `FrmCausasNoAplicarAP` |
| GET | `/regulations/options?fraction=` · POST `/regulations/accept` | `FrmRegulaciones` |
| POST | `/sanitary-notice/accept` | `FrmAvisoSanitario` + `MostrarMensajeAvisoSanitario` |
| POST | `/specifications/accept` · `/ieps/accept` · `/size-ranges/accept` | `FrmEspecificaciones` · `FrmIEPS` · `FrmRangoDeTallas` |
| POST | `/expiration/check` | `fun_validarfechavencimiento` (color de cada fecha) |

Los errores de negocio responden `{ meta: { statusCode, message } }` con el texto del escritorio. La diferencia de
costos de Guardar responde **409** con `meta.status = "COST_DIFFERENCE"`; el front pregunta "¿Desea continuar?" y
vuelve a mandar Guardar con `confirmCostDifference = true`. Abrir un código cuyo proveedor no tiene marcas responde
**404** con `meta.status = "NO_BRANDS"`; el front ofrece el alta de marca y vuelve a abrir el código.

El encabezado de la sesión trae `purchaseFraction` (Fracción FRC de compras, sólo Muebles) como referencia junto a
la fracción MXN.

## Reglas que viven aquí

| Regla | Origen |
|---|---|
| Peso en kg (kg, g, lb, oz), piezas que componen según UMC / unidad de tarifa, arancel a aplicar, cuota compensatoria (PIEZA, costo específico o por valor UMT), color del % de permiso automático | `FrmAduanas`, `AduanasDAL.GuardarAduanas` |
| Orden y textos de las validaciones de Información aduanal, regulaciones, aviso sanitario, especificaciones, IEPS y tallas | `ValidaCampo` / `ValidarControles` de cada formulario |
| Inventario (Muebles ÷ 100, Ropa importe ÷ existencia), costo general, marca por omisión, pieza 1 por omisión, combinado | `CargarInformacionCodigo`, `ComplementosAduanalesDAL` |
| Suma de piezas, cuota truncada, comparación de costos, aviso sanitario con la pieza representativa, ×100 del central de Muebles, valor UMT sólo con unidad 4 en el central de Ropa | `Guardar`, `GuardarComplementos`, `GrabarCentral` |

Diferencias deliberadas con el escritorio:

- `ValidaCampo(TXT_COMPOSICION)` revisaba la descripción aduanal en lugar de la composición; aquí Ropa exige la
  composición, que es lo que dice el mensaje.
- Se exige país de origen (el combo del escritorio siempre tenía uno seleccionado).
- Atajos de teclado y la navegación con Enter no aplican.

## Configuración y pruebas

`src/main/resources/application.yml` no se versiona; ver [`docs/application.example.yml`](docs/application.example.yml).

```bash
mvn test
```
