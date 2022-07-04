package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import urb.projects.facturas.config.serializers.NumberDeserializer;

@JsonPropertyOrder({ "condominio", "numero", "clave", "operacion", "cantidad" })
@Data
public class InvoiceCsvDto {

    String condominio;

    String numero;

    String operacion;

    @JsonDeserialize(using = NumberDeserializer.class)
    double cantidad;

    String clave;
  
}