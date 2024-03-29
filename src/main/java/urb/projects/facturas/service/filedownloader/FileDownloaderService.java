package urb.projects.facturas.service.filedownloader;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class FileDownloaderService {

    public byte[] getDownloadFile(String location) throws IOException {
        URL url = new URL(location);
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = url.openStream ();
            bytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) is.close();
        }
        return bytes;
    };


    public byte[] postDownloadFile(String location) throws IOException {
        URL url = new URL(location);

        InputStream is = null;
        byte[] bytes = null;
        try {
            is = url.openStream();
            bytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) is.close();
        }
        return bytes;
    };
    
}
