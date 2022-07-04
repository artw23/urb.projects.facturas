package urb.projects.facturas.service.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.filedownloader.FileDownloaderService;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

class InvoiceHttpServiceTest {

    @Mock
    private FileDownloaderService fileDownloaderService;

    @InjectMocks
    private InvoiceHttpService invoiceHttpService;


    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void downloadAndParseXML() throws Exception {
        String path = "./src/test/resources/";
        String fileName = "2022-07-0095-000084787626-ING-5198788-TIM.xml";

        byte [] output = Files.readAllBytes( Paths.get(path,fileName));

        when(fileDownloaderService.getDownloadFile(any())).thenReturn(output);

        InvoiceXmlDto invoiceXmlDto = invoiceHttpService.downloadAndParseXML("SomeUrl");
        assertEquals("ING", invoiceXmlDto.getSerie());
        assertEquals("5198788", invoiceXmlDto.getFolio());
        assertEquals(181, invoiceXmlDto.getTotal());
        assertEquals("G01", invoiceXmlDto.getReceptorXml().getUsoCfdi());
    }

}