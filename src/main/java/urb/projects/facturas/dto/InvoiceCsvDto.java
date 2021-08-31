package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import urb.projects.facturas.dto.serializers.NumberDeserializer;

@JsonPropertyOrder({ "condominio", "numero", "clave", "cantidad" })
@Data
public class InvoiceCsvDto {

    String condominio;

    String numero;

    @JsonDeserialize(using = NumberDeserializer.class)
    double cantidad;

    String clave;
  
}