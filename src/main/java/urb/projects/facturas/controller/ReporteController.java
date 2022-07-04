package urb.projects.facturas.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.InvoiceType;
import urb.projects.facturas.domain.Report;
import urb.projects.facturas.service.report.InvoiceService;
import urb.projects.facturas.service.report.ReportService;

@RestController
@RequestMapping("/reports")
public class ReporteController {

  private ReportService reporteService;

  private InvoiceService facturaService;

  public ReporteController(ReportService reporteService, InvoiceService facturaService) {
    this.reporteService = reporteService;
    this.facturaService = facturaService;
  }

  @GetMapping
  public Page<Report> getAllReports(
      @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return reporteService.getAll(pageable);
  }

  @PostMapping
  public Report createReport(@RequestParam("payment_date")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
                             @RequestParam("invoice_type") InvoiceType invoiceType,
                             @RequestParam("file") MultipartFile file) throws IOException {
    return reporteService.createReport(paymentDate, invoiceType, file);

  }

  @GetMapping(value = "/{id}/invoices")
  public Page<Factura> getReportInvoices(@PathVariable UUID id,
                                         @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return facturaService.getFacturasByReporeId(id, pageable);
  }

  @PostMapping(value = "/{id}/process")
  public ResponseEntity processReport(@PathVariable UUID id) throws Exception {
    reporteService.processReport(id);
    Thread.sleep(2000);
    return new ResponseEntity(HttpStatus.ACCEPTED);
  }


  @RequestMapping(value = "/{id}/download", produces = "application/zip")
  public ResponseEntity<StreamingResponseBody> downloadReport(@PathVariable UUID id) {
    return ResponseEntity
        .ok()
        .header("Content-Disposition", "attachment; filename=\"test.zip\"")
        .body(out -> {
          List<File> filesToDownload = reporteService.download(id);
          ZipOutputStream zipOutputStream = new ZipOutputStream(out);

          for (File fileToDownload : filesToDownload) {
            ZipEntry entry = new ZipEntry(fileToDownload.getNombre());
            entry.setSize(fileToDownload.getContent().length);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(fileToDownload.getContent());
            zipOutputStream.closeEntry();
          }

          zipOutputStream.close();
        });
  }

}
