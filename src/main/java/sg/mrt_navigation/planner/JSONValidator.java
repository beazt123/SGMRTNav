package sg.mrt_navigation.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class JSONValidator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final JsonSchema schema;

    public JSONValidator(URI pathToSchema) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        schema = factory.getSchema(pathToSchema);
        schema.initializeValidators();
    }

    public static JsonNode parse(String jsonStr) {
        JsonNode jsonObj = null;
        try {
            jsonObj = mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid JSON received: " + jsonStr);
        }
        return jsonObj;
    }

    public boolean isValidJson(JsonNode jsonObj)  {
        Set<ValidationMessage> errors = schema.validate(jsonObj);
//        System.out.println(errors);
        return errors.isEmpty();
    }

}
