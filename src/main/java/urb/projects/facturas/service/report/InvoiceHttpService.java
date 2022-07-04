package urb.projects.facturas.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import urb.projects.facturas.config.exceptions.InvoiceProcessException;
import urb.projects.facturas.domain.FacturaErrors;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceHttpListDto;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.FileService;
import urb.projects.facturas.service.filedownloader.FileDownloaderService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class InvoiceHttpService {

    private final static String PDF_BASE_URL = "http://200.79.74.185/cpd_all/show_pdf_cfdi.php";
    private final String XML_BASE_URL = "http://200.79.74.185/cpd_predial/descarga_xml_cfdi.php";
    private final String BASE_URL = "http://200.79.74.187/sello_digital/v2/functions.php";

    private final FileDownloaderService fileDownloaderService;
    private final FileService fileService;

    private final ObjectMapper objectMapper;

    private final XmlMapper xmlMapper;

    public InvoiceHttpService(FileDownloaderService fileDownloaderService, FileService fileService){
        this.fileDownloaderService = fileDownloaderService;
        this.fileService = fileService;
        this.objectMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    public InvoiceXmlDto downloadAndParseXML(String url) throws InvoiceProcessException {
        byte[] file = downloadXml(url);
        try {
            return xmlMapper.readValue(new String(file), InvoiceXmlDto.class);
        } catch (JsonProcessingException e) {
            throw new InvoiceProcessException(FacturaErrors.ERROR_PARSING_XML);
        }
    }


    public File downloadXml(String outputName, String url) throws InvoiceProcessException {
        byte[] file = downloadXml(url);
        return fileService.saveFile(outputName + ".xml", file);
    }

    private byte[] downloadXml(String url) throws InvoiceProcessException {
        String fileName = getFileNameFromUrl(url);

        String downloadUrl = UriComponentsBuilder
                .fromHttpUrl(XML_BASE_URL)
                .queryParam("urlxml", url)
                .queryParam("namexml", fileName)
                .toUriString();

        try {
            return fileDownloaderService.downloadFile(downloadUrl);
        } catch (IOException e) {
            throw new InvoiceProcessException(FacturaErrors.ERROR_DOWNLOADING_XML);
        }
    }

    public File downloadPdf(String outputName, String url) throws InvoiceProcessException {
        String fileName = getFileNameFromUrl(url);

        String downloadUrl = UriComponentsBuilder
                .fromHttpUrl(PDF_BASE_URL)
                .queryParam("urlpdf", url)
                .queryParam("namepdf", fileName)
                .toUriString();

        try {
            byte[] file = fileDownloaderService.downloadFile(downloadUrl);
            return fileService.saveFile(outputName + ".pdf", file);
        } catch (IOException e) {
            throw new InvoiceProcessException(FacturaErrors.ERROR_DOWNLOADING_PDF);
        }

    }

    public List<InvoiceHttpDto> getDiversosInvoices(String operacion, int year, double amount) throws InvoiceProcessException {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("tipoBusqueda", "D");
        headers.put("anio", year);
        headers.put("operacion", operacion);
        headers.put("importe", (int)amount);

        List<InvoiceHttpDto> invoiceHttpDtos = getInvoices(headers);
        if(invoiceHttpDtos.isEmpty()){
            throw new InvoiceProcessException(FacturaErrors.NO_INVOICE_FOUND);
        }
        return invoiceHttpDtos;

    }

    public List<InvoiceHttpDto> getPredialInvoices(String claveCatastral, int year) throws InvoiceProcessException {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("tipoBusqueda", "P");
        headers.put("anio", year);
        headers.put("operacion", claveCatastral);
        headers.put("importe", "1 OR 1=1");

        List<InvoiceHttpDto> invoiceHttpDtos = getInvoices(headers);
        if(invoiceHttpDtos.isEmpty()){
            throw new InvoiceProcessException(FacturaErrors.NO_INVOICE_FOUND);
        }
        return invoiceHttpDtos;
    }

    public List<InvoiceHttpDto> getPredialInvoices(String claveCatastral, int year, double amount) throws InvoiceProcessException {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("tipoBusqueda", "P");
        headers.put("anio", year);
        headers.put("operacion", claveCatastral);
        headers.put("importe", (int)amount);

        List<InvoiceHttpDto> invoiceHttpDtos = getInvoices(headers);
        if(invoiceHttpDtos.isEmpty()){
            invoiceHttpDtos = getPredialInvoices(claveCatastral, year);
        }

        return invoiceHttpDtos;
    }

    private List<InvoiceHttpDto> getInvoices(HashMap<String, Object> headers) throws InvoiceProcessException {
        String result =  WebClient.builder()
                .baseUrl(BASE_URL)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logRequest());
                })
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("tipoBusqueda", headers.get("tipoBusqueda"))
                        .queryParam("anio", headers.get("anio"))
                        .queryParam("operacion", headers.get("operacion"))
                        .queryParam("importe", headers.get("importe"))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .log()
                .block();

        try {
            InvoiceHttpListDto invoiceHttpListDto = objectMapper.readValue(result, InvoiceHttpListDto.class);
            return invoiceHttpListDto.getRecibos();
        } catch (JsonProcessingException e) {
            throw new InvoiceProcessException(FacturaErrors.ERROR_PARSING_INVOICE_RESPONSE);
        }

    }


    private String getFileNameFromUrl(String url){
        String[] paths = url.split("/");
        String filename = paths[paths.length-1];
        return filename.replace(".xml","").replace(".pdf","");
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

}
