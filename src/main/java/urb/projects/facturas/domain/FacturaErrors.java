package urb.projects.facturas.domain;

public enum FacturaErrors {
    ERROR_AL_CONSULTAR_EN_SITIO_WEB(""),
    SE_OBTUBOO_MAS_DE_UN_RESULTADO(""),
    NO_COINCIDE_CANTIDAD_CON_PAGINA_WEB(""),
    ERROR_AL_PROCESAR_XML(""),
    NO_COINCIDE_CANTIDAD_CON_XML(""),
    ERROR_AL_DESCARGAR_PDF(""),
    ERROR_AL_DESCARGAR_XML(""),
    NO_SE_ENCONTRO_RESULTADO(""),
    CANTIDADES_NO_COINCIDEN(""),
    NO_INVOICE_FOUND("No se encontro factura en sitio web"),
    NO_SE_ENCONTRO_RESULTADO_COM_FECHA(""),
    ERROR_DOWNLOADING_XML("Error al descargar XML"),
    ERROR_DOWNLOADING_PDF("Error al descargar PDF"),
    ERROR_PARSING_INVOICE_RESPONSE("Error al leer respuesta de busqueda"),
    ERROR_PARSING_XML("Error al leer XML"),
    WRONG_INVOICE_RETRIEVE("No coinciden datos con factura obtenida"),
    UNKNOW_ERROR("ERROR DESCONOCIDO");


    private String message;

    FacturaErrors(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
