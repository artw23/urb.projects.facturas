package urb.projects.facturas.domain;

import lombok.Data;
import java.util.List;

@Data
public class Invoice {

    String condominio;

    int numero;

    String claveCatastral;

    String factura;

    List<InvoiceErrors> errores;


}
