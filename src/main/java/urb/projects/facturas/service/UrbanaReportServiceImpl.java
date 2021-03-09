package urb.projects.facturas.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import urb.projects.facturas.domain.Invoice;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.filedownloader.PdfFileDownloaderServiceImpl;
import urb.projects.facturas.service.filedownloader.XmlFileDownloaderServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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

    private Environment env;

    @Override
    public void processInvoiceReport() {

        List<Invoice> result = new ArrayList<>();

        String fileName = env.getProperty("input.file");

        List<InvoiceCsvDto> invoiceCsvDtos = retrieveInvoicesFromCsv(fileName);

        result = invoiceCsvDtos.stream()
                .map(inv -> createInvoice(inv))
                .collect(Collectors.toList());

        writeResultsToCsv(result);

    }

    private void writeResultsToCsv(List<Invoice> result) {

        String out =  env.getProperty("output.dir") + "out.csv";
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
        String baseUrl = env.getProperty("output.dir");

        try {
            xmlFileDownloaderService.downloadFile(invoice.getXmlUrl(), baseUrl + invoice.getCondominio(), invoice.getFactura());
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_DESCARGAR_XML);
        }
    }

    private void retrievePdfFile(Invoice invoice) {
        String baseUrl = env.getProperty("output.dir");
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

        if(invoice.getCantidadInicial() != invoiceXmlDto.getTotal()){
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
            invoiceHttpListDto = retrieveAllInvoicesDataFromHttp(invoice);
            if(invoiceHttpListDto == null){
                invoice.addError(NO_SE_ENCONTRO_RESULTADO);
                return;
            }
        }

        if(invoiceHttpListDto.size() != 1){
            invoice.addError(SE_OBTUBOO_MAS_DE_UN_RESULTADO);
        }

        String rawDate =  env.getProperty("fecha.de.pago");
        LocalDate formattedDate = LocalDate.parse(String.format(rawDate, DateTimeFormatter.ISO_LOCAL_DATE));

        Optional<InvoiceHttpDto> optionalInvoiceHttpDto = invoiceHttpListDto.stream()
                .filter(inv -> inv.getFecha_pago().compareTo(formattedDate) == 0)
                .max(Comparator.comparing(InvoiceHttpDto::getFecha_pago));

        if(optionalInvoiceHttpDto.isEmpty()){
            invoice.addError(NO_SE_ENCONTRO_RESULTADO_COM_FECHA);
            return;
        }



        InvoiceHttpDto invoiceHttpDto = optionalInvoiceHttpDto.get();
        if(invoiceHttpDto.getImporte() != invoice.getCantidadInicial()){
            invoice.addError(CANTIDADES_NO_COINCIDEN);
        }

        invoice.setCantidadFinal(invoiceHttpDto.getImporte());
        invoice.setPeriodo(invoiceHttpDto.getPeriodo_inicial());
        invoice.setFecha(invoiceHttpDto.getFecha_pago());
        invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
        invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
    }

    private List<InvoiceHttpDto> retrieveAllInvoicesDataFromHttp(Invoice invoice) {
        List<InvoiceHttpDto> invoiceHttpListDto = new ArrayList<>();
        try {
            invoiceHttpListDto = invoiceHttpService.retrieveInvoice(invoice.getClaveCatastral(),2021);
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_CONSULTAR_EN_SITIO_WEB);
        }
        return invoiceHttpListDto;
    }

    private List<InvoiceHttpDto> retrieveInvoicesDataFromHttp(Invoice invoice){
        List<InvoiceHttpDto> invoiceHttpListDto = new ArrayList<>();
        try {
            invoiceHttpListDto = invoiceHttpService.retrieveInvoice(invoice.getClaveCatastral(),2021,invoice.getCantidadInicial());
        } catch (Exception e) {
            e.printStackTrace();
            invoice.addError(ERROR_AL_CONSULTAR_EN_SITIO_WEB);
        }
        return invoiceHttpListDto;
    }
}
