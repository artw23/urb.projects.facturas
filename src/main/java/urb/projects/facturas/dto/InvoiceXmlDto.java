package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Comprobante")
public class InvoiceXmlDto {

    @JacksonXmlProperty(isAttribute = true, localName = "Serie")
    String serie;

    @JacksonXmlProperty(isAttribute = true, localName = "Folio")
    String folio;

    @JacksonXmlProperty(isAttribute = true,  localName = "Total")
    int total;

    @JacksonXmlProperty( localName = "Receptor")
    ReceptorXml receptorXml;

}
