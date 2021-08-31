package urb.projects.facturas.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import urb.projects.facturas.domain.Factura;

public interface UrbanaReportService {

  String processInvoiceReportString(InputStream inputStream, String fechaPago) throws IOException;

  List<Factura> processInvoiceReportString();
    
}