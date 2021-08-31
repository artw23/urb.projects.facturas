package urb.projects.facturas.service.filedownloader;

public interface FileDownloaderService {

    byte[] downloadFile(String url) throws Exception;
    
}
