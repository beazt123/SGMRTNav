package sg.mrt_navigation.planner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.Assert.*;

public class JSONValidatorTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testTransitions() throws IOException, URISyntaxException {
        URI transitionsSchemaURI = this.getClass().getResource("/transitions-schema.json").toURI();
        URL transitionsExampleURI = this.getClass().getResource("/transitions-example.json");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = factory.getSchema(transitionsSchemaURI);
        schema.initializeValidators();

        JsonNode jsonObj = mapper.readTree(transitionsExampleURI);
        Set<ValidationMessage> errors = schema.validate(jsonObj);
        assertTrue(errors.isEmpty());
    }
    @Test
    public void testStationsList() throws IOException, URISyntaxException {
        URI transitionsSchemaURI = this.getClass().getResource("/station-list-schema.json").toURI();
        URL transitionsExampleURI = this.getClass().getResource("/station-list-example.json");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = factory.getSchema(transitionsSchemaURI);
        schema.initializeValidators();

        JsonNode jsonObj = mapper.readTree(transitionsExampleURI);
        Set<ValidationMessage> errors = schema.validate(jsonObj);
        assertTrue(errors.isEmpty());
    }
}