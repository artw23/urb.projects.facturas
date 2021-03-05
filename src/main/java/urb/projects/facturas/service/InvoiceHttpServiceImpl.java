package urb.projects.facturas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import urb.projects.facturas.dto.InvoiceHttpDto;
import urb.projects.facturas.dto.InvoiceHttpListDto;

import java.util.List;

@Service
public class InvoiceHttpServiceImpl implements InvoiceHttpService {

    @Override
    public List<InvoiceHttpDto> retrieveInvoice(String claveCatastral, int year, int amount) throws JsonProcessingException {
        return makeRequest(claveCatastral, year, amount);

    }


    private List<InvoiceHttpDto> makeRequest(String claveCatastral, int year, int amount) throws JsonProcessingException {
        WebClient client = WebClient.builder()
                .baseUrl("http://www.mqro.gob.mx/sello_digital/v2/functions.php")
                .build();

        String result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("tipoBusqueda", "P")
                        .queryParam("anio", year)
                        .queryParam("operacion", claveCatastral)
                        .queryParam("importe", amount)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .log()
                .block();
        ObjectMapper objectMapper = new ObjectMapper();

        InvoiceHttpListDto invoiceHttpListDto =  objectMapper.readValue(result, InvoiceHttpListDto.class);
        return invoiceHttpListDto.getRecibos();

    }

}
