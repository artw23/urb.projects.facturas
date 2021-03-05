package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceXmlDto {

    @JacksonXmlProperty(isAttribute = true, namespace = "cfdi:Comprobante", localName = "Serie")
    String serie;

    @JacksonXmlProperty(isAttribute = true, namespace = "cfdi:Comprobante", localName = "Folio")
    String folio;

    @JacksonXmlProperty(isAttribute = true, namespace = "cfdi:Comprobante", localName = "Total")
    int total;
}
