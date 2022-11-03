package urb.projects.facturas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Comprobante")
public class InvoiceXmlDto {

    @JacksonXmlProperty(isAttribute = true, localName = "Serie")
    String serie;

    @JacksonXmlProperty(isAttribute = true, localName = "Folio")
    String folio;

    @JacksonXmlProperty(isAttribute = true, localName = "Fecha")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate fecha;

    @JacksonXmlProperty(isAttribute = true,  localName = "Total")
    int total;

    @JacksonXmlProperty( localName = "Receptor")
    ReceptorXml receptorXml;

}
