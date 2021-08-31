package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import urb.projects.facturas.dto.serializers.NumberDeserializer;

import java.time.LocalDate;

@Data
public class InvoiceHttpDto {

    private String archivo_pdf;

    private String archivo_xml;

    private String clave_catastral;

    private String contribucion;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yy")
    private LocalDate fecha_pago;

    @JsonDeserialize(using = NumberDeserializer.class)
    private double importe;

    private String no_liquidacion;

    private String nombre_archivo;

    private String periodo_final;

    private String periodo_inicial;
  
}