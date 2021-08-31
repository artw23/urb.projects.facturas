package urb.projects.facturas.service.filedownloader;

import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class XmlFileDownloaderServiceImpl implements FileDownloaderService {

    @Override
    public byte[] downloadFile(String url) throws Exception {
        String[] bits = url.split("/");
        String lastOne = bits[bits.length-1];
        lastOne = lastOne.replace(".xml","");

        String baseUrl = "http://200.79.74.185/cpd_predial/descarga_xml_cfdi.php?";
        baseUrl = baseUrl + "urlxml=" + url;
        baseUrl = baseUrl + "&namexml=" + lastOne;

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
            //handle errors
        }
        finally {
            if (is != null) is.close();
        }
        return bytes;
    }

    public String downloadXmlString(String url) throws Exception{

        String[] bits = url.split("/");
        String lastOne = bits[bits.length-1];
        String endlastOne = lastOne.replace(".xml","");

        WebClient client = WebClient.builder()
                .baseUrl("http://200.79.74.185/cpd_predial/descarga_xml_cfdi.php")
                .build();

        String result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("urlxml", url)
                        .queryParam("namexml", endlastOne)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .log()
                .block();
        return result;
    }

}
