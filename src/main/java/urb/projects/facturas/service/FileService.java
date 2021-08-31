package urb.projects.facturas.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import urb.projects.facturas.domain.File;
import urb.projects.facturas.domain.FileRepository;

@Service
public class FileService {

  FileRepository fileRepository;

  public FileService(FileRepository fileRepository){
    this.fileRepository = fileRepository;
  }

  public File saveFile(String nombre, MultipartFile multipartFile) throws IOException {
    return this.saveFile(nombre, multipartFile.getBytes());
  }

  public List<File> findById(List<UUID> uuids){
    return fileRepository.findAllById(uuids);
  }

  public File saveFile(String nombre, byte[] bytes) throws IOException {
    File file = new File();
    file.setNombre(nombre);
    file.setContent(bytes);
    return fileRepository.save(file);
  }



  public File getFile(UUID id) throws IOException {
    return fileRepository.getOne(id);
  }

}
