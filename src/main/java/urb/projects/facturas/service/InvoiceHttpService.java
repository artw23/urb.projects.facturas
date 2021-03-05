package urb.projects.facturas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceHttpListDto;

import java.util.List;

public interface InvoiceHttpService {

    List<InvoiceHttpDto> retrieveInvoice(String claveCatastral, int year, int amount) throws Exception;

}