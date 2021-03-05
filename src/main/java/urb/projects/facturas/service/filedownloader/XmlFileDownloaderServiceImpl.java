package urb.projects.facturas.service.filedownloader;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class XmlFileDownloaderServiceImpl implements FileDownloaderService {

    @Override
    public void downloadFile(String url, String path, String fileName) throws IOException {

        verifyDirectory(path);

        String[] bits = url.split("/");
        String lastOne = bits[bits.length-1];
        lastOne = lastOne.replace(".xml","");

        String baseUrl = "http://200.79.74.185/cpd_predial/descarga_xml_cfdi.php?";
        baseUrl = baseUrl + "urlxml=" + url;
        baseUrl = baseUrl + "&namexml=" + lastOne;
        FileUtils.copyURLToFile(
                new URL(baseUrl),
                new File(path+"/"+fileName+".xml"),
                10000, 10000);

    }

    public String downloadXmlString(String url){

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
                .block();
        return result;
    }

    private void verifyDirectory(String path){
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }
}
