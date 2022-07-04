package urb.projects.facturas.service.report;

import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.InvoiceType;

import java.util.List;

public interface InvoiceProcessorService {

    boolean canHandle(InvoiceType invoiceType);

    void processInvoices(Factura invoices);

}
