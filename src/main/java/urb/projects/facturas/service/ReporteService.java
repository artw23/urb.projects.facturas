package urb.projects.facturas.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.ReportStatus;
import urb.projects.facturas.domain.Reporte;
import urb.projects.facturas.domain.ReporteRepository;

@Service
public class ReporteService {

  private ReporteRepository reporteRepository;
  private FacturaService facturaService;
  private FileService fileService;

  public ReporteService(ReporteRepository reporteRepository, FacturaService facturaService, FileService fileService){
    this.reporteRepository = reporteRepository;
    this.facturaService = facturaService;
    this.fileService = fileService;
  }

  public Reporte create(LocalDate fechaDePago, MultipartFile multipartFile) throws IOException {
    Reporte reporte = new Reporte();
    reporte.setFechaDePago(fechaDePago);
    reporte.setStatus(ReportStatus.CREATED);

    File file = fileService.saveFile(fechaDePago.toString(), multipartFile);
    reporte.setInFileId(file.getId());

    reporte = reporteRepository.save(reporte);
    facturaService.createFacturas(reporte.getId(), multipartFile, fechaDePago);

    return reporteRepository.save(reporte);
  }

  public Page<Reporte> getAll(Pageable pageable) {
    return reporteRepository.findAll(pageable);

  }

  public void run(UUID id) {
    updateStatus(id, ReportStatus.RUNNING);
    facturaService.processAll(id);
    updateStatus(id, ReportStatus.SUCCESS);
  }

  private void updateStatus(UUID id, ReportStatus status) {
    Reporte reporte = reporteRepository.findById(id).orElseThrow();
    reporte.setStatus(status);
    reporteRepository.save(reporte);
  }

  public List<File> download(UUID reportId) {
    List<Factura> facturas = facturaService.getFacturasByReporeId(reportId);
    List<UUID> filesUuids = new ArrayList<>();
    for (Factura factura: facturas) {
      if(factura.getPdfFileId() != null){
        filesUuids.add(factura.getPdfFileId());
      }

      if(factura.getXmlfileId() != null){
        filesUuids.add(factura.getXmlfileId());
      }
    }
    return fileService.findById(filesUuids);
  }
}
