package urb.projects.facturas.service;

public interface InvoiceHttpService {

    void retrieveInvoice(String claveCatastral, int year, String amount);

}