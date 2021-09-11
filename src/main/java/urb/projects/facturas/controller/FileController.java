package urb.projects.facturas.controller;

import java.io.IOException;
import java.util.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.service.FileService;

@RestController
@RequestMapping("/file")
public class FileController {

  private FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Resource> getById(@PathVariable UUID id) throws IOException {
    File file = fileService.getFile(id);

    byte bytes [] = file.getContent();
    ByteArrayResource resource = new ByteArrayResource(file.getContent());

    return ResponseEntity.ok()
        .contentLength(bytes.length)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

}