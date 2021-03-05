package urb.projects.facturas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import urb.projects.facturas.domain.Invoice;
import urb.projects.facturas.domain.InvoiceErrors;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceHttpListDto;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.filedownloader.PdfFileDownloaderServiceImpl;
import urb.projects.facturas.service.filedownloader.XmlFileDownloaderServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static urb.projects.facturas.domain.InvoiceErrors.*;

@Service
@AllArgsConstructor
@Slf4j
public class UrbanaReportServiceImpl implements UrbanaReportService {

    private InvoiceHttpService invoiceHttpService;

    private PdfFileDownloaderServiceImpl pdfFileDownloaderService;

    private XmlFileDownloaderServiceImpl xmlFileDownloaderService;

    private CsvParserServiceImpl csvParserService;

    @Override
    public void processInvoiceReport(String fileName) {

        List<Invoice> result = new ArrayList<>();

        List<InvoiceCsvDto> invoiceCsvDtos = retrieveInvoicesFromCsv(fileName);

        result = invoiceCsvDtos.parallelStream()
                .map(inv -> createInvoice(inv))
                .collect(Collectors.toList());

        writeResultsToCsv(result);

    }

    private void writeResultsToCsv(List<Invoice> result) {

        String out = "C:\\Users\\dapaj\\IdeaProjects\\facturacion\\src\\main\\resources\\out\\out.csv";
        try {
            csvParserService.writeCsv(result,out, Invoice.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  List<InvoiceCsvDto> retrieveInvoicesFromCsv(String fileName){
        List<InvoiceCsvDto> invoiceCsvDtos = new ArrayList<>();

        try {
            invoiceCsvDtos = csvParserService.parseCsv(fileName,InvoiceCsvDto.class );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invoiceCsvDtos;
    }

    private Invoice createInvoice(InvoiceCsvDto invoiceCsvDto){

        Invoice invoice = Invoice.from(invoiceCsvDto);

        processInvoiceHttp(invoice);
        if(invoice.getXmlUrl() == null) return invoice;
        processInvoiceXml(invoice);
        if(invoice.getFactura() == null) return invoice;
        retrievePdfFile(invoice);
        retrieveXmlFile(invoice);

        return invoice;
    }

    private void retrieveXmlFile(Invoice invoice) {
        String baseUrl = "C:\\Users\\dapaj\\IdeaProjects\\facturacion\\src\\main\\resources\\out\\";

        try {
            xmlFileDownloaderService.downloadFile(invoice.getXmlUrl(), baseUrl + invoice.getCondominio(), invoice.getFactura());
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_DESCARGAR_XML);
        }
    }

    private void retrievePdfFile(Invoice invoice) {
        String baseUrl = "C:\\Users\\dapaj\\IdeaProjects\\facturacion\\src\\main\\resources\\out\\";
        try {
            pdfFileDownloaderService.downloadFile(invoice.getPdfUrl(), baseUrl + invoice.getCondominio(),  invoice.getFactura());
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_DESCARGAR_PDF);
        }

    }

    private void processInvoiceXml(Invoice invoice) {
        Optional<InvoiceXmlDto> invoiceXmlDtoOptional  = retrieveInvoiceXml(invoice);

        if(invoiceXmlDtoOptional.isEmpty()){
            return;
        }

        InvoiceXmlDto invoiceXmlDto = invoiceXmlDtoOptional.get();

        if(invoice.getCantidad() != invoiceXmlDto.getTotal()){
            invoice.addError(NO_COINCIDE_CANTIDAD_CON_XML);
        }

        invoice.setFactura(invoiceXmlDto.getSerie() + invoiceXmlDto.getFolio());

    }

    private Optional<InvoiceXmlDto> retrieveInvoiceXml(Invoice invoice){

        InvoiceXmlDto invoiceXmlDto = null;

        String xmlResult = null;
        try {
            xmlResult = xmlFileDownloaderService.downloadXmlString(invoice.getXmlUrl());
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_PROCESAR_XML);
            return Optional.ofNullable(invoiceXmlDto);
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            invoiceXmlDto  = xmlMapper.readValue(xmlResult, InvoiceXmlDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_PROCESAR_XML);
        }
        return Optional.ofNullable(invoiceXmlDto);

    }

    private void processInvoiceHttp(Invoice invoice){
        List<InvoiceHttpDto> invoiceHttpListDto = retrieveInvoicesDataFromHttp(invoice);

        if(invoiceHttpListDto == null){
            invoice.addError(SE_OBTUBOO_MAS_DE_UN_RESULTADO);
            return;
        }

        if(invoiceHttpListDto.size() != 1){
            invoice.addError(SE_OBTUBOO_MAS_DE_UN_RESULTADO);
        }

        Optional<InvoiceHttpDto> optionalInvoiceHttpDto = invoiceHttpListDto.stream()
                .filter(inv -> inv.getImporte() == invoice.getCantidad())
                .findFirst();

        if(optionalInvoiceHttpDto.isEmpty()){
            invoice.addError(NO_COINCIDE_CANTIDAD_CON_PAGINA_WEB);
            return;
        }

        InvoiceHttpDto invoiceHttpDto = optionalInvoiceHttpDto.get();
        invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
        invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
    }

    private List<InvoiceHttpDto> retrieveInvoicesDataFromHttp(Invoice invoice){
        List<InvoiceHttpDto> invoiceHttpListDto = new ArrayList<>();
        try {
            invoiceHttpListDto = invoiceHttpService.retrieveInvoice(invoice.getClaveCatastral(),2021,invoice.getCantidad());
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_CONSULTAR_EN_SITIO_WEB);
        }
        return invoiceHttpListDto;
    }
}
