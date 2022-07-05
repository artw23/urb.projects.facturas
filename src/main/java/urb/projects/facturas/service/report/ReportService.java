package urb.projects.facturas.service.report;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import urb.projects.facturas.domain.*;
import urb.projects.facturas.service.FileService;

@Service
@Slf4j
public class ReportService {

  private ReporteRepository reporteRepository;
  private InvoiceService invoiceService;
  private FileService fileService;

  public ReportService(ReporteRepository reporteRepository, InvoiceService invoiceService, FileService fileService){
    this.reporteRepository = reporteRepository;
    this.invoiceService = invoiceService;
    this.fileService = fileService;
  }

  public Report createReport(LocalDate paymentDate, InvoiceType invoiceType, MultipartFile multipartFile) throws IOException {

    File file = fileService.saveFile(paymentDate.toString(), multipartFile);

    Report report =Report.builder()
            .paymentDate(paymentDate)
            .status(ReportStatus.CREATED)
            .invoiceType(invoiceType)
            .inputFileId(file.getId())
            .build();

    report = reporteRepository.save(report);

    invoiceService.createInvoice(report.getId(), multipartFile, paymentDate);
    return reporteRepository.save(report);
  }

  public Page<Report> getAll(Pageable pageable) {
    return reporteRepository.findAll(pageable);
  }


  @Async
  public void processReport(UUID id) {
    Report report = reporteRepository.findById(id).orElseThrow();
    updateStatus(report, ReportStatus.RUNNING);

    invoiceService.processReportInvoices(id, report.getInvoiceType());

    updateStatus(report, ReportStatus.SUCCESS);
    log.info("Finished processing async");
  }

  private void updateStatus(Report report, ReportStatus status) {
    log.info("Updating report {} to status {}", report.getId(), status);
    report.setStatus(status);
    reporteRepository.save(report);
  }

  @Transactional
  public List<UUID> getInvoicesToDowlnoad(UUID reportId) {
    List<UUID> pdfFiles=  invoiceService.getFacturasByReporeId(reportId)
            .stream()
            .filter(invoice -> invoice.getPdfFileId() != null)
            .map(Factura::getPdfFileId)
            .collect(Collectors.toList());

    List<UUID> xmlFile=  invoiceService.getFacturasByReporeId(reportId)
            .stream()
            .filter(invoice -> invoice.getXmlfileId() != null)
            .map(Factura::getXmlfileId)
            .collect(Collectors.toList());

    pdfFiles.addAll(xmlFile);
    return pdfFiles;
  }

  @Transactional
  public List<UUID> getRecieptsToDowlnoad(UUID reportId) {
    return invoiceService.getFacturasByReporeId(reportId)
            .stream()
            .filter(invoice -> invoice.getRecepitFileId() != null)
            .map(Factura::getRecepitFileId)
            .collect(Collectors.toList());
  }

}
