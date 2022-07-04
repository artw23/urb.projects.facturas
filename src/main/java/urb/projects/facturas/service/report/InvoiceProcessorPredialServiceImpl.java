package urb.projects.facturas.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import urb.projects.facturas.config.exceptions.InvoiceProcessException;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.FacturaErrors;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.InvoiceType;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceXmlDto;

import java.util.List;

@Service
public class InvoiceProcessorPredialServiceImpl  implements InvoiceProcessorService{

    private final static String G01 = "G01";
    private InvoiceHttpService invoiceHttpService;

    public InvoiceProcessorPredialServiceImpl(InvoiceHttpService invoiceHttpService){
        this.invoiceHttpService =  invoiceHttpService;
    }

    @Override
    public boolean canHandle(InvoiceType invoiceType) {
        return InvoiceType.Predial.equals(invoiceType);
    }

    @Override
    public void processInvoices(List<Factura> invoices){
        for(Factura invoice: invoices){
            try {
                processInvoice(invoice);
            } catch (InvoiceProcessException e) {
                invoice.addError(e.getFacturaErrors());
            } catch (Exception e){
                invoice.addError(FacturaErrors.UNKNOW_ERROR, e);
            }
        }
    }

    private void processInvoice(Factura invoice) throws InvoiceProcessException {
        getG01Invoice(invoice);

        File pdfFile = invoiceHttpService.downloadPdf(getInvoiceFileName(invoice), invoice.getNombreFactura());
        invoice.setPdfFileId(pdfFile.getId());

        File xmlFile = invoiceHttpService.downloadXml(getInvoiceFileName(invoice), invoice.getNombreFactura());
        invoice.setXmlfileId(xmlFile.getId());
    }

    private void getG01Invoice(Factura invoice) throws InvoiceProcessException {
        List<InvoiceHttpDto> invoiceHttpListDto = invoiceHttpService.getPredialInvoices(invoice.getClaveCatastral(), invoice.getFecha().getYear(), invoice.getCantidadInicial());

        for(InvoiceHttpDto invoiceHttpDto: invoiceHttpListDto){
            InvoiceXmlDto invoiceXmlDto = invoiceHttpService.downloadAndParseXML(invoiceHttpDto.getArchivo_xml());

            if(isCorrectInvoice(invoiceXmlDto, invoice)){
                invoice.setNombreFactura(invoiceXmlDto.getSerie() + invoiceXmlDto.getFolio());
                invoice.setCantidadFinal(invoiceHttpDto.getImporte());
                invoice.setPeriodo(invoiceHttpDto.getPeriodo_inicial());
                invoice.setFecha(invoiceHttpDto.getFecha_pago());
                invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
                invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
                return;
            }
        }
        throw  new InvoiceProcessException(FacturaErrors.WRONG_INVOICE_RETRIEVE);
    }

    private boolean isCorrectInvoice(InvoiceXmlDto invoiceXmlDto, Factura invoice){
        if(G01.equalsIgnoreCase(invoiceXmlDto.getReceptorXml().getUsoCfdi()) &&
                invoiceXmlDto.getTotal() == invoice.getCantidadInicial()){
            return true;
        }
        return false;
    }

    private String getInvoiceFileName(Factura invoice){
        return new StringBuilder("/")
                .append(invoice.getCondominio())
                .append("/01-MPIO QRO-")
                .append(invoice.getNombreFactura())
                .toString();
    }
}
