package urb.projects.facturas.service.filedownloader;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;

@Service
public class PdfFileDownloaderServiceImpl implements FileDownloaderService {
    @Override
    public byte[] downloadFile(String url) throws Exception {

        String[] bits = url.split("/");
        String lastOne = bits[bits.length-1];
        lastOne = lastOne.replace(".pdf","");

        String baseUrl = "http://200.79.74.185/cpd_all/show_pdf_cfdi.php?";
        baseUrl = baseUrl + "urlpdf=" + url;
        baseUrl = baseUrl + "&namepdf=" + lastOne;

        return fetchRemoteFile(baseUrl);
    }

    private byte[] fetchRemoteFile(String location) throws Exception {
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
    }

    private void verifyDirectory(String path){
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }
}
