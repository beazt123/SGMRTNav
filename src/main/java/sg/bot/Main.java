package sg.bot;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.db.MapDBContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.logging.Logger;

public class Main {
    public static Logger LOGGER = Logger.getLogger(Main.class.getName());


    public static void main(String[] args) {
        DB db = DBMaker.memoryDB().make();
        DBContext dbContext = new MapDBContext(db);
        String botToken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_USERNAME");

        try {
            // Create the TelegramBotsApi object to register your bots
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Register your newly created AbilityBot
            botsApi.registerBot(
                    new Bot(
                            botToken,
                            botUsername,
                            dbContext)
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
