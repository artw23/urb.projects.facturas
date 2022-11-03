package urb.projects.facturas.config.exceptions;

import lombok.Data;
import urb.projects.facturas.domain.FacturaErrors;

@Data
public class InvoiceProcessException extends Exception{

    FacturaErrors facturaErrors;

    String description;

    public InvoiceProcessException(FacturaErrors facturaErrors){
        super(facturaErrors.getMessage());
        this.facturaErrors = facturaErrors;
    }

    public InvoiceProcessException(FacturaErrors facturaErrors, String description){
        super(facturaErrors.getMessage());
        this.facturaErrors = facturaErrors;
        this.description = description;
    }
}
