package urb.projects.facturas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceXmlDto;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({"condominio", "numero", "claveCatastral", "cantidad", "factura", "errores"})
public class Invoice {

    String condominio;

    String numero;

    int cantidad;

    String claveCatastral;

    String factura;

    @JsonIgnore
    String pdfUrl;

    @JsonIgnore
    String xmlUrl;

    List<InvoiceErrors> errores = new ArrayList<>();


    public static Invoice from(InvoiceCsvDto invoiceCsvDto) {
        Invoice invoice = new Invoice();
        invoice.setCondominio(invoiceCsvDto.getCondominio());
        invoice.setClaveCatastral(invoiceCsvDto.getClave());
        invoice.setCantidad(invoiceCsvDto.getCantidad());
        invoice.setNumero(invoiceCsvDto.getNumero());
        return invoice;
    }

    public void addError(InvoiceErrors invoiceErrors){
        errores.add(invoiceErrors);
    }
}
