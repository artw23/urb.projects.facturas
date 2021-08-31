package urb.projects.facturas;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import urb.projects.facturas.service.UrbanaReportService;


@SpringBootApplication
@AllArgsConstructor
@EnableJpaAuditing
public class Application{

    private UrbanaReportService urbanaReportService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
