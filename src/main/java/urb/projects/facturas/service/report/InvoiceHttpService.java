package urb.projects.facturas.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import urb.projects.facturas.config.exceptions.InvoiceProcessException;
import urb.projects.facturas.domain.FacturaErrors;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceHttpListDto;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.FileService;
import urb.projects.facturas.service.filedownloader.FileDownloaderService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InvoiceHttpService {

    private final static String PDF_BASE_URL = "http://200.79.74.185/cpd_all/show_pdf_cfdi.php";

    private final static String RECIBO_BASE_URL = "http://200.79.74.187/cpd_predial/reporte/show_pdf.php";
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

        log.info("Downloading pdf {} for invoice {}", downloadUrl, fileName);

        try {
            return fileDownloaderService.getDownloadFile(downloadUrl);
        } catch (IOException e) {
            throw new InvoiceProcessException(FacturaErrors.ERROR_DOWNLOADING_XML);
        }
    }

    public File downloadReciept(String outputName, String noLiquidacion) throws InvoiceProcessException {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("conc_liq_key",noLiquidacion);
        formData.add("tipotramite","pdf");

        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        byte[] result =  WebClient.builder()
                .baseUrl(RECIBO_BASE_URL)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block()
                .getByteArray();

        return fileService.saveFile(outputName + ".pdf", result);
    }

    public File downloadPdf(String outputName, String url) throws InvoiceProcessException {
        String fileName = getFileNameFromUrl(url);

        String downloadUrl = UriComponentsBuilder
                .fromHttpUrl(PDF_BASE_URL)
                .queryParam("urlpdf", url)
                .queryParam("namepdf", fileName)
                .toUriString();

        log.info("Downloading pdf {} for invoice {}", downloadUrl, outputName);

        try {
            byte[] file = fileDownloaderService.getDownloadFile(downloadUrl);
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
                .block();

        try {
            InvoiceHttpListDto invoiceHttpListDto = objectMapper.readValue(result, InvoiceHttpListDto.class);
            return invoiceHttpListDto.getRecibos() != null ? invoiceHttpListDto.getRecibos() : new ArrayList<InvoiceHttpDto>();
        } catch (JsonProcessingException e) {
            throw new InvoiceProcessException(FacturaErrors.ERROR_PARSING_INVOICE_RESPONSE);
        }

    }


    private String getFileNameFromUrl(String url){
        String[] paths = url.split("/");
        String filename = paths[paths.length-1];
        return filename.replace(".xml","").replace(".pdf","");
    }

}
