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
    public void prepareSchemaFromFactory() throws IOException {
        Path fileName
                = Path.of("D:\\Desktop\\External-Commitments\\Projects\\Java-Telegram-Bot\\SGMRTNav\\docs\\station-list-schema.json");
        String str = Files.readString(fileName);
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        System.out.println(factory.getSchema(str));
    }
    @Test
    public void validateJson() throws IOException {
        Path schemaFileName
                = Path.of("D:\\Desktop\\External-Commitments\\Projects\\Java-Telegram-Bot\\SGMRTNav\\docs\\station-list-schema.json");
        Path jsonFileName
                = Path.of("D:\\Desktop\\External-Commitments\\Projects\\Java-Telegram-Bot\\SGMRTNav\\docs\\station-list-example.json");
        String str = Files.readString(schemaFileName);
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = factory.getSchema(str);
        schema.initializeValidators();

        String jsonExample = Files.readString(jsonFileName);
        JsonNode jsonObj = mapper.readTree(jsonExample);
        Set<ValidationMessage> errors = schema.validate(jsonObj);


    }

    @Test
    public void t() {
        URL l = this.getClass().getResource("/la");
        System.out.println(l.toString());
    }

}
