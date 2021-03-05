package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import urb.projects.facturas.domain.InvoiceErrors;

import java.util.List;

@JsonPropertyOrder({ "condominio", "numero", "clave", "cantidad" })
@Data
public class InvoiceCsvDto {

    String condominio;

    String numero;

    int cantidad;

    String clave;
  
}