package urb.projects.facturas.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.Reporte;
import urb.projects.facturas.service.ReporteService;

@RestController
@RequestMapping("/reporte")
public class ReporteController {

  private ReporteService reporteService;

  public ReporteController(ReporteService reporteService) {
    this.reporteService = reporteService;
  }

  @GetMapping
  public Page<Reporte> getAll(Pageable pageable) {
    return reporteService.getAll(pageable);
  }

  @PostMapping
  public Reporte create(@RequestParam("fecha_de_pago")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDePago,
      @RequestParam("file") MultipartFile file) throws IOException {

    return reporteService.create(fechaDePago, file);

  }

  @PostMapping(value = "/{id}/run")
  public ResponseEntity run(@PathVariable UUID id) throws IOException {
     reporteService.run(id);
     return new ResponseEntity(HttpStatus.ACCEPTED);

  }


  @RequestMapping(value="/{id}/download", produces="application/zip")
  public ResponseEntity<StreamingResponseBody> zipFiles(@PathVariable UUID id) {
    return ResponseEntity
        .ok()
        .header("Content-Disposition", "attachment; filename=\"test.zip\"")
        .body(out -> {
          List<File> filesToDownload = reporteService.download(id);
          ZipOutputStream zipOutputStream = new ZipOutputStream(out);

          for (File fileToDownload: filesToDownload) {
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
