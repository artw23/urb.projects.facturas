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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    private void processInvoice(Factura invoice) throws InvoiceProcessException {
        getG01Invoice(invoice);

        File pdfFile = invoiceHttpService.downloadPdf(getInvoiceFileName(invoice), invoice.getNombreFactura());
        invoice.setPdfFileId(pdfFile.getId());

        File xmlFile = invoiceHttpService.downloadXml(getInvoiceFileName(invoice), invoice.getNombreFactura());
        invoice.setXmlfileId(xmlFile.getId());
    }

    private void getG01Invoice(Factura invoice) throws InvoiceProcessException {
        List<InvoiceHttpDto> invoiceHttpListDto = invoiceHttpService.getPredialInvoices(invoice.getClaveCatastral(), invoice.getFecha().getYear(), invoice.getCantidadInicial());

        Map<InvoiceXmlDto, InvoiceHttpDto> map = new HashMap<>();
        for(InvoiceHttpDto invoiceHttpDto: invoiceHttpListDto){
            map.put(invoiceHttpService.downloadAndParseXML(invoiceHttpDto.getArchivo_xml()), invoiceHttpDto);
        }

        Optional<InvoiceXmlDto> g01invoice = map.keySet()
                .stream()
                .filter(invoiceXmlDto -> G01.equalsIgnoreCase(invoiceXmlDto.getReceptorXml().getUsoCfdi()))
                .findFirst();

        if(g01invoice.isEmpty()){
            invoice.addError(FacturaErrors.NO_G01_INVOICE);

            List<InvoiceXmlDto>  matchedInvoice = map.keySet()
                    .stream()
                    .filter(invoiceXmlDto -> invoiceXmlDto.getTotal() == invoice.getCantidadInicial())
                    .collect(Collectors.toList());

            if(matchedInvoice.isEmpty()){
                throw new InvoiceProcessException(FacturaErrors.AMOUNT_DONT_MATCH);
            }

            InvoiceXmlDto finalMatch = matchedInvoice.stream()
                    .filter(matched -> {
                        InvoiceHttpDto invoiceHttpDto = map.get(matched);
                        return invoiceHttpDto.getFecha_pago().equals(invoice.getFecha());
                    })
                    .findFirst()
                    .orElseThrow(() -> new InvoiceProcessException(FacturaErrors.AMOUNT_MATCHED_BUT_NOT_DATE));

            InvoiceHttpDto invoiceHttpDto = map.get(finalMatch);

            File recieptFile = invoiceHttpService.downloadReciept(getReciboFileName(invoice), invoiceHttpDto.getNo_liquidacion());
            invoice.setRecepitFileId(recieptFile.getId());
            throw new InvoiceProcessException(FacturaErrors.INVOICE_WITH_MATCH_PRICE_AND_DATE);

        }else if(g01invoice.get().getTotal() == invoice.getCantidadInicial()){
            InvoiceXmlDto invoiceXmlDto = g01invoice.get();
            InvoiceHttpDto invoiceHttpDto = map.get(invoiceXmlDto);

            invoice.setNombreFactura(invoiceXmlDto.getSerie() + invoiceXmlDto.getFolio());
            invoice.setCantidadFinal(invoiceHttpDto.getImporte());
            invoice.setPeriodo(invoiceHttpDto.getPeriodo_inicial());
            invoice.setFecha(invoiceHttpDto.getFecha_pago());
            invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
            invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
            return;
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

    private String getReciboFileName(Factura invoice){
        return new StringBuilder("/")
                .append(invoice.getCondominio())
                .append("/recibo-")
                .append(invoice.getNumero())
                .append("-clave-")
                .append(invoice.getClaveCatastral())
                .toString();
    }
}
