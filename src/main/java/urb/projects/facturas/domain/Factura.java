package urb.projects.facturas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;
import urb.projects.facturas.dto.InvoiceCsvDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Factura extends BaseEntity{

    @Column(name = "reporte_id")
    private UUID reporteId;

    @Column(name = "condominio")
    private String condominio;

    @Column(name = "numero")
    private String numero;

    @Column(name = "clave_catastral")
    private String claveCatastral;

    @Column(name = "cantidad_inicial")
    private double cantidadInicial;

    @Column(name = "cantidad_final")
    private double cantidadFinal;

    @Column(name = "nombre_factura")
    private String nombreFactura;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "periodo")
    private String periodo;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "xml_url")
    private String xmlUrl;

    @Column(name = "pdf_file_id")
    private UUID pdfFileId ;

    @Column(name = "xml_file_id")
    private UUID xmlfileId;

    @Column(name = "errores")
    @Enumerated
    @ElementCollection(targetClass = FacturaErrors.class)
    private List<FacturaErrors> errores;

    public void addError(FacturaErrors error) {
        if(errores == null){
            errores = new ArrayList<>();
        }
        errores.add(error);
    }

}
