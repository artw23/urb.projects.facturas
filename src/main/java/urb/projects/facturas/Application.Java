package urb.projects.facturas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.service.CsvParserService;
import urb.projects.facturas.service.UrbanaReportService;

import java.util.List;


@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        CsvParserService csvParserService = null;
        UrbanaReportService urbanaReportService = null;

        List<InvoiceCsvDto> input = csvParserService.parseCsv("", InvoiceCsvDto.class);

        urbanaReportService.processInvoiceReport("");
    }

}
