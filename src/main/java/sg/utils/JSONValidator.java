package sg.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

public class JSONValidator {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final JsonSchema schema;
    private final String pathToSchema;


    public static class Result {

        private boolean isValid;

        private Set<String> msgs;
        private String schemaPath;

        private Result(boolean isValid, Set<String> msgs, String pathToSchema) {
            this.isValid = isValid;
            this.msgs = msgs;
            this.schemaPath = pathToSchema;
        }

        public String getSchemaPath() {
            return schemaPath;
        }

        public boolean isValid() {
            return isValid;
        }

        public Set<String> getMsgs() {
            return msgs;
        }

    }
    public JSONValidator(URI pathToSchema) {
        this.pathToSchema = pathToSchema.toString();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        schema = factory.getSchema(pathToSchema);
        schema.initializeValidators();
    }

    public JsonSchema getSchema() {
        return schema;
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

    public Result checkForValidity(JsonNode jsonObj)  {
        Set<ValidationMessage> errors = schema.validate(jsonObj);

        return new Result(
                errors.isEmpty(),
                errors.stream().map(msg -> msg.getMessage() + " at " + msg.getSchemaPath()).collect(Collectors.toSet()),
                pathToSchema
        );

    }

}
