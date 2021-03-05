package urb.projects.facturas.service.filedownloader;

import java.io.IOException;
import java.net.MalformedURLException;

public interface FileDownloaderService {

    void downloadFile(String url, String path, String fileName) throws IOException;
    
}
