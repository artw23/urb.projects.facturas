package urb.projects.facturas.service.report;

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
public class InvoiceProcessorDiversosServiceImpl implements  InvoiceProcessorService{

    private final static String G01 = "G01";
    private InvoiceHttpService invoiceHttpService;

    public InvoiceProcessorDiversosServiceImpl(InvoiceHttpService invoiceHttpService){
        this.invoiceHttpService =  invoiceHttpService;
    }

    @Override
    public boolean canHandle(InvoiceType invoiceType) {
        return InvoiceType.Diversos.equals(invoiceType);
    }

    @Override
    public void processInvoices(List<Factura> invoices) throws Exception {

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

    private void processInvoice(Factura invoice) throws Exception {

        getG01Invoice(invoice);

        File xmlFile = invoiceHttpService.downloadXml(getInvoiceFileName(invoice), invoice.getXmlUrl());
        invoice.setXmlfileId(xmlFile.getId());

        File pdfFile = invoiceHttpService.downloadPdf(getInvoiceFileName(invoice), invoice.getPdfUrl());
        invoice.setPdfFileId(pdfFile.getId());
    }

    private InvoiceHttpDto getG01Invoice(Factura invoice) throws InvoiceProcessException {
        List<InvoiceHttpDto> invoiceHttpListDto = invoiceHttpService.getDiversosInvoices(invoice.getOperacion(), invoice.getFecha().getYear(), invoice.getCantidadInicial());

        for(InvoiceHttpDto invoiceHttpDto: invoiceHttpListDto){
            InvoiceXmlDto invoiceXmlDto = invoiceHttpService.downloadAndParseXML(invoiceHttpDto.getArchivo_xml());

            if(G01.equalsIgnoreCase(invoiceXmlDto.getReceptorXml().getUsoCfdi())){
                invoice.setNombreFactura(invoiceXmlDto.getSerie() + invoiceXmlDto.getFolio());
                invoice.setCantidadFinal(invoiceHttpDto.getImporte());
                invoice.setPeriodo(invoiceHttpDto.getPeriodo_inicial());
                invoice.setFecha(invoiceHttpDto.getFecha_pago());
                invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
                invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
                return invoiceHttpDto;
            }
        }
        throw  new InvoiceProcessException(FacturaErrors.WRONG_INVOICE_RETRIEVE);
    }

    private String getInvoiceFileName(Factura invoice){
        return new StringBuilder("/")
                .append(invoice.getCondominio())
                .append("/01-MPIO QRO-")
                .append(invoice.getNombreFactura())
                .toString();
    }

}
