package urb.projects.facturas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import javax.persistence.*;

import lombok.Data;
import org.springframework.util.StringUtils;
import urb.projects.facturas.dto.InvoiceCsvDto;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "factura")
public class Factura extends BaseEntity{

    @Column(name = "reporte_id")
    private UUID reporteId;

    @Column(name = "condominio")
    private String condominio;

    @Column(name = "numero")
    private String numero;

    @Column(name = "clave_catastral")
    private String claveCatastral;

    @Column(name = "operacion")
    private String operacion;

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

    @Column(name = "receipt_file_id")
    private UUID recepitFileId;
    @ElementCollection
    @Column(name = "errores")
    private Set<String> errores;

    public void addError(FacturaErrors error) {
        if(this.errores == null){
            this.errores = new HashSet<>();
        }
        this.errores.add(error.getMessage());
    }

    public void addError(FacturaErrors error, String message) {
        if(this.errores == null){
            this.errores = new HashSet<>();
        }
        if(!StringUtils.isEmpty(message)){
            this.errores.add(error.getMessage() + message);
        }else{
            this.errores.add(error.getMessage());
        }

    }

    public void addError(FacturaErrors error, Exception e) {
        if(this.errores == null){
            this.errores = new HashSet<>();
        }
        this.errores.add(error.getMessage() + "\n" + e.getMessage());
    }

}
