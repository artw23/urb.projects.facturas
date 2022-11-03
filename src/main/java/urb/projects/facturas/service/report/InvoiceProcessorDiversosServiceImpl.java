package urb.projects.facturas.service.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import urb.projects.facturas.config.exceptions.InvoiceProcessException;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.FacturaErrors;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.InvoiceType;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceXmlDto;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    public void processInvoices(Factura invoice){

            try {
                processInvoice(invoice);
            } catch (InvoiceProcessException e) {
                invoice.addError(e.getFacturaErrors());
            } catch (Exception e){
                log.error("Error processing invoice", e);
                invoice.addError(FacturaErrors.UNKNOW_ERROR, e);
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

        Map<InvoiceXmlDto, InvoiceHttpDto> map = new HashMap<>();
        for(InvoiceHttpDto invoiceHttpDto: invoiceHttpListDto){
            map.put(invoiceHttpService.downloadAndParseXML(invoiceHttpDto.getArchivo_xml()), invoiceHttpDto);
        }

        Optional<InvoiceXmlDto> matchedInvoice = map.keySet()
                .stream()
                .filter(invoiceXmlDto -> G01.equalsIgnoreCase(invoiceXmlDto.getReceptorXml().getUsoCfdi()))
                .filter(invoiceXmlDto -> invoice.getFecha().equals(invoiceXmlDto.getFecha()))
                .filter(invoiceXmlDto ->  invoiceXmlDto.getTotal() == invoice.getCantidadInicial())
                .findFirst();

        if(matchedInvoice.isEmpty()){
            List<String> foundInvoices = map.keySet()
                    .stream()
                    .map(inv -> {
                        return "CFDI: " + inv.getReceptorXml().getUsoCfdi() + " Fecha: " + inv.getFecha() + " Total: " + inv.getTotal();
                    })
                    .collect(Collectors.toList());
            throw new InvoiceProcessException(FacturaErrors.WRONG_INVOICE_RETRIEVE, String.join("\n", foundInvoices));
        }

        InvoiceXmlDto invoiceXmlDto = matchedInvoice.get();
        InvoiceHttpDto invoiceHttpDto = map.get(invoiceXmlDto);

        invoice.setNombreFactura(invoiceXmlDto.getSerie() + invoiceXmlDto.getFolio());
        invoice.setCantidadFinal(invoiceHttpDto.getImporte());
        invoice.setPeriodo(invoiceHttpDto.getPeriodo_inicial());
        invoice.setFecha(invoiceHttpDto.getFecha_pago());
        invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
        invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
        return invoiceHttpDto;

    }

    private String getInvoiceFileName(Factura invoice){
        return new StringBuilder("/")
                .append(invoice.getCondominio())
                .append("/01-MPIO QRO-")
                .append(invoice.getNombreFactura())
                .toString();
    }


    private String getReciboFileName(Factura invoice){
        return new StringBuilder("/")
                .append(invoice.getCondominio())
                .append("/recibo-")
                .append(invoice.getNumero())
                .append("-operacion-")
                .append(invoice.getOperacion())
                .toString();
    }

}
