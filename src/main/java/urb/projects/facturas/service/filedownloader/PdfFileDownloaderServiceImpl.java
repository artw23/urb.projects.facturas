package urb.projects.facturas.service.filedownloader;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class PdfFileDownloaderServiceImpl implements FileDownloaderService {
    @Override
    public void downloadFile(String url, String path, String fileName) throws Exception {

        verifyDirectory(path);

        String[] bits = url.split("/");
        String lastOne = bits[bits.length-1];
        lastOne = lastOne.replace(".pdf","");

        String baseUrl = "http://200.79.74.185/cpd_all/show_pdf_cfdi.php?";
        baseUrl = baseUrl + "urlpdf=" + url;
        baseUrl = baseUrl + "&namepdf=" + lastOne;
        FileUtils.copyURLToFile(
                new URL(baseUrl),
                new File(path+"/"+fileName+".pdf"),
                10000, 10000);

    }

    private void verifyDirectory(String path){
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }
}
