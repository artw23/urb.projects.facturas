package urb.projects.facturas.dto.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class Numberdeserializer extends JsonDeserializer<Double> {


    @Override
    public Double deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String input = jsonParser.readValueAs(String.class);
        input = input.replace(",","");
        input = input.replace("$","");
        return Double.parseDouble(input);
    }
}
