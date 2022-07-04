package urb.projects.facturas.domain;

public enum FacturaErrors {
    NO_INVOICE_FOUND("No se encontro factura en sitio web\n"),
    NO_SE_ENCONTRO_RESULTADO_COM_FECHA(""),
    ERROR_DOWNLOADING_XML("Error al descargar XML\n"),
    ERROR_DOWNLOADING_PDF("Error al descargar PDF\n"),
    ERROR_PARSING_INVOICE_RESPONSE("Error al leer respuesta de busqueda\n"),
    ERROR_PARSING_XML("Error al leer XML\n"),
    WRONG_INVOICE_RETRIEVE("No coinciden datos con factura obtenida\n"),
    UNKNOW_ERROR("ERROR DESCONOCIDO\n"),
    NO_G01_INVOICE("No se encontro factura G01\n"),
    AMOUNT_DONT_MATCH("Las cantidades no coinciden\n"),
    INVOICE_WITH_MATCH_PRICE_AND_DATE("Se encontro recibo con misma cantidad y fecha\n"),
    AMOUNT_MATCHED_BUT_NOT_DATE("Se encontro recibo misma cantidad pero diferente fecha\n");


    private String message;

    FacturaErrors(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
