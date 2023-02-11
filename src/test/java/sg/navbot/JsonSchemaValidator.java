package sg.navbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.Test;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class JsonSchemaValidator {
    private ObjectMapper mapper = new ObjectMapper();
    @Test
    public void prepareSchemaFromFactory() throws URISyntaxException, IOException {
        Path fileName
                = Path.of(JsonSchemaValidator.class.getResource("/station-list-schema.json").toURI());
        String str = Files.readString(fileName);
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        System.out.println(factory.getSchema(str));
    }
    @Test
    public void validateJson() throws IOException, URISyntaxException {
        Path schemaFileName
                = Path.of(JsonSchemaValidator.class.getResource("/station-list-schema.json").toURI());
        Path jsonFileName
                = Path.of(JsonSchemaValidator.class.getResource("/station-list-example.json").toURI());
        String str = Files.readString(schemaFileName);
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        JsonSchema schema = factory.getSchema(str);
        schema.initializeValidators();

        String jsonExample = Files.readString(jsonFileName);
        JsonNode jsonObj = mapper.readTree(jsonExample);
        Set<ValidationMessage> errors = schema.validate(jsonObj);


    }

}
