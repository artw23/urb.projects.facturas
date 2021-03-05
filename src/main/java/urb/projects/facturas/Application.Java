package urb.projects.facturas;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import urb.projects.facturas.dto.InvoiceCsvDto;
import urb.projects.facturas.service.CsvParserService;
import urb.projects.facturas.service.UrbanaReportService;

import java.util.List;


@SpringBootApplication
@AllArgsConstructor
public class Application implements CommandLineRunner {

    private UrbanaReportService urbanaReportService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        urbanaReportService.processInvoiceReport("C:\\Users\\dapaj\\IdeaProjects\\facturacion\\src\\main\\resources\\out\\example.csv");
    }

}
