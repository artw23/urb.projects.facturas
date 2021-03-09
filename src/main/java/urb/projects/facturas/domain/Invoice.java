package urb.projects.facturas.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import urb.projects.facturas.dto.InvoiceCsvDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonPropertyOrder({"condominio", "numero", "claveCatastral", "cantidadInicial", "cantidadFinal", "periodo", "factura", "fecha", "errores"})
public class Invoice {

    String condominio;

    String numero;

    int cantidadInicial;

    double cantidadFinal;

    String claveCatastral;

    String factura;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate fecha;

    String periodo;

    @JsonIgnore
    String pdfUrl;

    @JsonIgnore
    String xmlUrl;

    List<InvoiceErrors> errores = new ArrayList<>();


    public static Invoice from(InvoiceCsvDto invoiceCsvDto) {
        Invoice invoice = new Invoice();
        invoice.setCondominio(invoiceCsvDto.getCondominio());
        invoice.setClaveCatastral(invoiceCsvDto.getClave());
        invoice.setCantidadInicial(invoiceCsvDto.getCantidad());
        invoice.setNumero(invoiceCsvDto.getNumero());
        return invoice;
    }

    public void addError(InvoiceErrors invoiceErrors){
        errores.add(invoiceErrors);
    }
}
