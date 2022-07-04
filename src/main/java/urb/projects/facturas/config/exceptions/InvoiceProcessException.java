package urb.projects.facturas.config.exceptions;

import lombok.Data;
import urb.projects.facturas.domain.FacturaErrors;

@Data
public class InvoiceProcessException extends Exception{

    FacturaErrors facturaErrors;
    public InvoiceProcessException(FacturaErrors facturaErrors){
        super(facturaErrors.getMessage());
        this.facturaErrors = facturaErrors;
    }
}
