package urb.projects.facturas.dto;

import lombok.Data;

@Data
public class InvoiceHttpDto {

    String archivo_pdf;
    String archivo_xml;
    String clave_catastral;
    String contribucion;
    String fecha_pago;
    double importe;
    String no_liquidacion;
    String nombre_archivo;
    String periodo_final;
    String periodo_inicial;
  
}