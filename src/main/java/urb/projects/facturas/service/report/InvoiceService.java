package urb.projects.facturas.service.report;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.FacturaRepository;
import urb.projects.facturas.domain.InvoiceType;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.service.CsvParserService;

@Service
@Transactional
@Slf4j
public class InvoiceService {

  private FacturaRepository facturaRepository;
  private CsvParserService csvParserService;
  private List<InvoiceProcessorService> invoiceProcessors;

  public InvoiceService(FacturaRepository facturaRepository, CsvParserService csvParserService,
                        List<InvoiceProcessorService> invoiceProcessors) {
    this.facturaRepository = facturaRepository;
    this.csvParserService = csvParserService;
    this.invoiceProcessors = invoiceProcessors;
  }

  public Page<Factura> getFacturasByReporeId(UUID reporteId, Pageable pageable) {
    return facturaRepository.findByReporteId(reporteId, pageable);
  }

  public List<Factura> getFacturasByReporeId(UUID reporteId) {
    return facturaRepository.findByReporteId(reporteId);
  }

  public void createInvoice(UUID id, MultipartFile file, LocalDate fechaPago) throws IOException {
    List<InvoiceCsvDto> invoiceCsvDtos = retrieveInvoicesFromCsv(file.getInputStream());

    List<Factura> result = new ArrayList<>();

    result = invoiceCsvDtos.stream()
        .map(inv -> crearFacturaDeCsv(id, inv, fechaPago))
        .collect(Collectors.toList());
  }


  public void processReportInvoices(UUID reporteId, InvoiceType invoiceType){
    List<Factura> invoices = facturaRepository.findByReporteId(reporteId);

    InvoiceProcessorService invoiceProcessorService = invoiceProcessors.stream()
            .filter(processor -> processor.canHandle(invoiceType))
            .findFirst()
            .orElseThrow();

    invoices.parallelStream()
            .forEach(invoice -> {
              invoiceProcessorService.processInvoices(invoice);
              facturaRepository.save(invoice);
            });
    log.info("Finished processing {} requests", invoices.size());
  }

  private Factura crearFacturaDeCsv(UUID id, InvoiceCsvDto invoiceCsvDto,
                                    LocalDate fechaPago) {
    Factura factura = new Factura();
    factura.setReporteId(id);
    factura.setCondominio(invoiceCsvDto.getCondominio());
    factura.setClaveCatastral(invoiceCsvDto.getClave());
    factura.setCantidadInicial(invoiceCsvDto.getCantidad());
    factura.setNumero(invoiceCsvDto.getNumero());
    factura.setFecha(fechaPago);
    factura.setOperacion(invoiceCsvDto.getOperacion());

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

  public void deleteInvoiceByReportId(UUID reportId) {
    facturaRepository.deleteByReporteId(reportId);
  }
}
