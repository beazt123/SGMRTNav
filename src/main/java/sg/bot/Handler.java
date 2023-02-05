package sg.bot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.db.MapDBContext;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.io.*;
import java.util.HashMap;

public class Handler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static WebhookBot bot;
    static {
        DB db = DBMaker.memoryDB().make();
        DBContext dbContext = new MapDBContext(db);
        bot = new Buddy(
                System.getenv("BOT_TOKEN"),
                System.getenv("BOT_USERNAME"),
                dbContext
        );
        bot.onRegister();
    }

//    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        String input = reader.readLine();
//        System.out.println(input);
//        ObjectMapper mapper = new ObjectMapper();
//        JsonFactory factory = mapper.getFactory();
//        JsonParser parser = factory.createParser(input);
//        JsonNode actualObj = mapper.readTree(parser);
//        String updateStr = actualObj.get("body").asText();
//
//        Update upd = mapper.readValue(updateStr, Update.class);
//        bot.onWebhookUpdateReceived(upd);
//        reader.close();
//        outputStream.close();
//    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        System.out.println("input: " + input);
        System.out.println("context: " + context);
        ObjectMapper mapper = new ObjectMapper();
        Update upd = null;
        try {
            upd = mapper.readValue(input.getBody(), Update.class);
            System.out.println("upd: " + upd.toString());
            bot.onWebhookUpdateReceived(upd);
            System.out.println("Done processing update");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        return response;
    }
}
