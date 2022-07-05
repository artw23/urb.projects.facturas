package urb.projects.facturas.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.InvoiceType;
import urb.projects.facturas.domain.Report;
import urb.projects.facturas.service.FileService;
import urb.projects.facturas.service.report.InvoiceService;
import urb.projects.facturas.service.report.ReportService;

import javax.swing.text.html.Option;

@RestController
@RequestMapping("/reports")
@Slf4j
public class ReporteController {

    private ReportService reporteService;

    private InvoiceService facturaService;

    private FileService fileService;

    public ReporteController(ReportService reporteService, InvoiceService facturaService, FileService fileService) {
        this.reporteService = reporteService;
        this.facturaService = facturaService;
        this.fileService = fileService;
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

    @DeleteMapping(value = "/{id}")
    public void deleteReport(@PathVariable UUID id) throws IOException {
        reporteService.deleteReport(id);
    }

    @GetMapping(value = "/{id}/invoices")
    public Page<Factura> getReportInvoices(@PathVariable UUID id,
                                           @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return facturaService.getFacturasByReporeId(id, pageable);
    }

    @PostMapping(value = "/{id}/process")
    public ResponseEntity processReport(@PathVariable UUID id) throws Exception {
        reporteService.processReport(id);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }


    @RequestMapping(value = "/{id}/download", produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> downloadReport(@PathVariable UUID id) {
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"facturas.zip\"")
                .body(out -> {
                    List<UUID> filesToDownload = reporteService.getInvoicesToDowlnoad(id);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(out);

                    log.info("Starting compressing files");
                    for (UUID fileToDownload : filesToDownload) {
                        Optional<File> optionalFile = fileService.findById(fileToDownload);

                        if(optionalFile.isPresent()){
                            File file = optionalFile.get();
                            ZipEntry entry = new ZipEntry(file.getNombre());
                            entry.setSize(file.getContent().length);
                            zipOutputStream.putNextEntry(entry);
                            zipOutputStream.write(file.getContent());
                            zipOutputStream.closeEntry();
                        }
                    }
                    log.info("Finished compressing files");
                    zipOutputStream.close();
                });
    }

    @RequestMapping(value = "/{id}/reciepts", produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> downloadReciepts(@PathVariable UUID id) {
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"recibos.zip\"")
                .body(out -> {

                    List<UUID> filesToDownload = reporteService.getRecieptsToDowlnoad(id);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(out);

                    log.info("Starting compressing files");
                    int count = 1;
                    for (UUID fileToDownload : filesToDownload) {

                        log.info("Downloading file {} of {}", count ++, filesToDownload.size());
                        Optional<File> optionalFile = fileService.findById(fileToDownload);

                        if(optionalFile.isPresent()){
                            File file = optionalFile.get();
                            ZipEntry entry = new ZipEntry(file.getNombre());
                            entry.setSize(file.getContent().length);
                            zipOutputStream.putNextEntry(entry);
                            zipOutputStream.write(file.getContent());
                            zipOutputStream.closeEntry();
                        }
                    }
                    log.info("Finished compressing files");
                    zipOutputStream.close();
                });
    }

}
