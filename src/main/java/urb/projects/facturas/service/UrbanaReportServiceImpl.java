package urb.projects.facturas.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceXmlDto;
import urb.projects.facturas.service.filedownloader.PdfFileDownloaderServiceImpl;
import urb.projects.facturas.service.filedownloader.XmlFileDownloaderServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static urb.projects.facturas.domain.FacturaErrors.*;

@Service
@AllArgsConstructor
@Slf4j
public class UrbanaReportServiceImpl implements UrbanaReportService {

  private InvoiceHttpService invoiceHttpService;

  private PdfFileDownloaderServiceImpl pdfFileDownloaderService;

  private XmlFileDownloaderServiceImpl xmlFileDownloaderService;

  private CsvParserServiceImpl csvParserService;

  private Environment env;

  @Override
  public String processInvoiceReportString(InputStream inputStream, String fechaPago)
      throws IOException {

      List<InvoiceCsvDto> invoiceCsvDtos = retrieveInvoicesFromCsv(inputStream);

      List<Factura> result = new ArrayList<>();

      writeResultsToCsv(result);

      return csvParserService.listToCsvString(result, Factura.class);

  }

  @Override
  public List<Factura> processInvoiceReportString() {

    List<Factura> result = new ArrayList<>();

    String fileName = env.getProperty("input.file");

    List<InvoiceCsvDto> invoiceCsvDtos = retrieveInvoicesFromCsv(fileName);

    String date = env.getProperty("fecha.de.pago");


    writeResultsToCsv(result);
    return result;

  }

  private void writeResultsToCsv(List<Factura> result) {

    String out = env.getProperty("output.dir") + "out.csv";
    try {
      csvParserService.writeCsv(result, out, Factura.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<InvoiceCsvDto> retrieveInvoicesFromCsv(String fileName) {
    List<InvoiceCsvDto> invoiceCsvDtos = new ArrayList<>();

    try {
      invoiceCsvDtos = csvParserService.parseCsv(fileName, InvoiceCsvDto.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return invoiceCsvDtos;
  }

    private List<InvoiceCsvDto> retrieveInvoicesFromCsv(InputStream file) {
        List<InvoiceCsvDto> invoiceCsvDtos = new ArrayList<>();

        try {
            invoiceCsvDtos = csvParserService.parseCsv(file, InvoiceCsvDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invoiceCsvDtos;
    }




}
