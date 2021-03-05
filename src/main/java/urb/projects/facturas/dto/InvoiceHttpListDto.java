package urb.projects.facturas.dto;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceHttpListDto {

    List<InvoiceHttpDto> recibos;
}
