package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import urb.projects.facturas.dto.serializers.Numberdeserializer;

import java.util.Date;

@Data
public class InvoiceHttpDto {

    private String archivo_pdf;

    private String archivo_xml;

    private String clave_catastral;

    private String contribucion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yy")
    private Date fecha_pago;

    @JsonDeserialize(using = Numberdeserializer.class)
    private double importe;

    private String no_liquidacion;

    private String nombre_archivo;

    private String periodo_final;

    private String periodo_inicial;
  
}