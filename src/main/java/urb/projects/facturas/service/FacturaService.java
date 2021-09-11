package urb.projects.facturas.service;

import static urb.projects.facturas.domain.FacturaErrors.CANTIDADES_NO_COINCIDEN;
import static urb.projects.facturas.domain.FacturaErrors.ERROR_AL_CONSULTAR_EN_SITIO_WEB;
import static urb.projects.facturas.domain.FacturaErrors.ERROR_AL_DESCARGAR_PDF;
import static urb.projects.facturas.domain.FacturaErrors.ERROR_AL_DESCARGAR_XML;
import static urb.projects.facturas.domain.FacturaErrors.ERROR_AL_PROCESAR_XML;
import static urb.projects.facturas.domain.FacturaErrors.NO_COINCIDE_CANTIDAD_CON_XML;
import static urb.projects.facturas.domain.FacturaErrors.NO_SE_ENCONTRO_RESULTADO;
import static urb.projects.facturas.domain.FacturaErrors.NO_SE_ENCONTRO_RESULTADO_COM_FECHA;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.FacturaRepository;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.filedownloader.PdfFileDownloaderServiceImpl;
import urb.projects.facturas.service.filedownloader.XmlFileDownloaderServiceImpl;

@Service
@Transactional
public class FacturaService {

  private FacturaRepository facturaRepository;
  private CsvParserService csvParserService;
  private InvoiceHttpService invoiceHttpService;
  private FileService fileService;
  private PdfFileDownloaderServiceImpl pdfFileDownloaderService;
  private XmlFileDownloaderServiceImpl xmlFileDownloaderService;

  public FacturaService(FacturaRepository facturaRepository, CsvParserService csvParserService,
      InvoiceHttpService invoiceHttpService, PdfFileDownloaderServiceImpl pdfFileDownloaderService,
      XmlFileDownloaderServiceImpl xmlFileDownloaderService, FileService fileService) {
    this.facturaRepository = facturaRepository;
    this.csvParserService = csvParserService;
    this.invoiceHttpService = invoiceHttpService;
    this.pdfFileDownloaderService = pdfFileDownloaderService;
    this.xmlFileDownloaderService = xmlFileDownloaderService;
    this.fileService = fileService;
  }

  public Page<Factura> getFacturasByReporeId(UUID reporteId, Pageable pageable) {
    return facturaRepository.findByReporteId(reporteId, pageable);
  }

  public List<Factura> getFacturasByReporeId(UUID reporteId) {
    return facturaRepository.findByReporteId(reporteId);
  }

  public void createFacturas(UUID id, MultipartFile file, LocalDate fechaPago) throws IOException {
    List<InvoiceCsvDto> invoiceCsvDtos = retrieveInvoicesFromCsv(file.getInputStream());

    List<Factura> result = new ArrayList<>();

    result = invoiceCsvDtos.stream()
        .map(inv -> crearFacturaDeCsv(id, inv, fechaPago))
        .collect(Collectors.toList());

  }

  public Factura crearFacturaDeCsv(UUID id, InvoiceCsvDto invoiceCsvDto,
      LocalDate fechaPago) {
    Factura factura = new Factura();
    factura.setReporteId(id);
    factura.setCondominio(invoiceCsvDto.getCondominio());
    factura.setClaveCatastral(invoiceCsvDto.getClave());
    factura.setCantidadInicial(invoiceCsvDto.getCantidad());
    factura.setNumero(invoiceCsvDto.getNumero());
    factura.setFecha(fechaPago);

    return facturaRepository.save(factura);
  }


  private List<InvoiceCsvDto> retrieveInvoicesFromCsv(InputStream file) {
    List<InvoiceCsvDto> invoiceCsvDtos = new ArrayList<>();

    try {
      invoiceCsvDtos = csvParserService.parseCsv(file, InvoiceCsvDto.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return invoiceCsvDtos;
  }

  public List<Factura> processAll(UUID reporteId) {
    List<Factura> result = facturaRepository.findByReporteId(reporteId);
    result.forEach(factura -> processInvoiceHttp(factura));
    result.forEach(factura -> processInvoiceXml(factura));
    result.forEach(factura -> dowloadXml(factura));
    result.forEach(factura -> downloadPdf(factura));
    return result;
  }

  private void processInvoiceXml(Factura invoice) {
    Optional<InvoiceXmlDto> invoiceXmlDtoOptional = retrieveInvoiceXml(invoice);

    if (invoiceXmlDtoOptional.isEmpty()) {
      return;
    }

    InvoiceXmlDto invoiceXmlDto = invoiceXmlDtoOptional.get();

    if (invoice.getCantidadInicial() != invoiceXmlDto.getTotal()) {
      invoice.addError(CANTIDADES_NO_COINCIDEN);
    }

    invoice.setNombreFactura(invoiceXmlDto.getSerie() + invoiceXmlDto.getFolio());
    facturaRepository.save(invoice);

  }

  private Optional<InvoiceXmlDto> retrieveInvoiceXml(Factura invoice) {

    InvoiceXmlDto invoiceXmlDto = null;

    String xmlResult = null;
    try {
      xmlResult = xmlFileDownloaderService.downloadXmlString(invoice.getXmlUrl());
    } catch (Exception e) {
      e.printStackTrace();
      invoice.addError(ERROR_AL_DESCARGAR_XML);
    }

    XmlMapper xmlMapper = new XmlMapper();
    try {
      invoiceXmlDto = xmlMapper.readValue(xmlResult, InvoiceXmlDto.class);
    } catch (Exception e) {
      e.printStackTrace();
      invoice.addError(ERROR_AL_PROCESAR_XML);
    }
    return Optional.ofNullable(invoiceXmlDto);

  }

  private void dowloadXml(Factura invoice) {
    try {
      byte [] bytes = xmlFileDownloaderService.downloadFile(invoice.getXmlUrl());
      File file = fileService.saveFile("/"+invoice.getCondominio()+"/01-MPIO QRO-"+invoice.getNombreFactura()+".xml",bytes);
      invoice.setXmlfileId(file.getId());
    } catch (Exception e) {
      invoice.addError(ERROR_AL_DESCARGAR_XML);
      e.printStackTrace();
    }
    facturaRepository.save(invoice);
  }

  private void downloadPdf(Factura invoice) {
    try {
      byte [] bytes = pdfFileDownloaderService.downloadFile(invoice.getPdfUrl());
      File file = fileService.saveFile("/"+invoice.getCondominio()+"/01-MPIO QRO-"+invoice.getNombreFactura()+".pdf",bytes);
      invoice.setPdfFileId(file.getId());
    } catch (Exception e) {
      e.printStackTrace();
      invoice.addError(ERROR_AL_DESCARGAR_PDF);
    }
    facturaRepository.save(invoice);

  }


  private void processInvoiceHttp(Factura invoice) {
    List<InvoiceHttpDto> invoiceHttpListDto = retrieveInvoicesDataFromHttp(invoice);

    if (invoiceHttpListDto == null) {
      invoiceHttpListDto = retrieveAllInvoicesDataFromHttp(invoice);
      if (invoiceHttpListDto == null) {
        invoice.addError(NO_SE_ENCONTRO_RESULTADO);
        return;
      }
    }

    LocalDate formattedDate = invoice.getFecha();

    Optional<InvoiceHttpDto> optionalInvoiceHttpDto = invoiceHttpListDto.stream()
        .filter(inv -> inv.getFecha_pago().compareTo(formattedDate) == 0)
        .max(Comparator.comparing(InvoiceHttpDto::getFecha_pago));

    if (optionalInvoiceHttpDto.isEmpty()) {
      invoice.addError(NO_SE_ENCONTRO_RESULTADO_COM_FECHA);
      return;
    }

    InvoiceHttpDto invoiceHttpDto = optionalInvoiceHttpDto.get();
    if (invoiceHttpDto.getImporte() != invoice.getCantidadInicial()) {
      invoice.addError(CANTIDADES_NO_COINCIDEN);
    }

    invoice.setCantidadFinal(invoiceHttpDto.getImporte());
    invoice.setPeriodo(invoiceHttpDto.getPeriodo_inicial());
    invoice.setFecha(invoiceHttpDto.getFecha_pago());
    invoice.setPdfUrl(invoiceHttpDto.getArchivo_pdf());
    invoice.setXmlUrl(invoiceHttpDto.getArchivo_xml());
    facturaRepository.save(invoice);
  }

  private List<InvoiceHttpDto> retrieveAllInvoicesDataFromHttp(Factura invoice) {
    List<InvoiceHttpDto> invoiceHttpListDto = new ArrayList<>();
    try {
      invoiceHttpListDto = invoiceHttpService.retrieveInvoice(invoice.getClaveCatastral(), 2021);
    } catch (Exception e) {
      e.printStackTrace();
      invoice.addError(ERROR_AL_CONSULTAR_EN_SITIO_WEB);
    }
    return invoiceHttpListDto;
  }

  private List<InvoiceHttpDto> retrieveInvoicesDataFromHttp(Factura invoice) {
    List<InvoiceHttpDto> invoiceHttpListDto = new ArrayList<>();
    try {
      invoiceHttpListDto = invoiceHttpService
          .retrieveInvoice(invoice.getClaveCatastral(), 2021, invoice.getCantidadInicial());
    } catch (Exception e) {
      e.printStackTrace();
      invoice.addError(ERROR_AL_CONSULTAR_EN_SITIO_WEB);
    }
    return invoiceHttpListDto;
  }
}
