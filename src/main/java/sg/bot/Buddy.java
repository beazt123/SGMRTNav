package sg.bot;


import org.telegram.abilitybots.api.bot.AbilityWebhookBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.util.AbilityExtension;
import sg.bot.extensions.MRTRoutePlannerAbility;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;


public class Buddy extends AbilityWebhookBot {
    protected Buddy(String botToken, String botUsername, DBContext dbContext) {
        super(botToken, botUsername, "", dbContext); //bot path is not used because it will be set manually outside of java.
        final AbilityExtension[] userDefinedAbilities = {
                new MRTRoutePlannerAbility(this),
        };
        for (AbilityExtension a : userDefinedAbilities) {

            addExtensions(a);
        }
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .info("Tap to get started with this bot!")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String msg = "Hi there!\nI can help you reduce the walking distance between MRT transits & exits. Tap /go to start!";
                    silent.send(msg, ctx.chatId());
                })
                .build();
    }

    @Override
    public long creatorId() {
        return 0;
    }

}
