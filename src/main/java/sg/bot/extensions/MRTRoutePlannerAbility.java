package sg.bot.extensions;

import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyFlow;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.domain.Stations;
import sg.mrt_navigation.planner.MRTNetwork;
import sg.mrt_navigation.planner.TrainRoutePlanner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;
import static sg.utils.UserInputProcessing.*;

public class MRTRoutePlannerAbility implements AbilityExtension {
    private BaseAbilityBot extensionUser;
    private static final TrainRoutePlanner planner = new TrainRoutePlanner(MRTNetwork.mrtNetwork);
    private static final List<String> stations = Stations.getAllStations()
                                                        .stream()
                                                        .map(s -> s.toString())
                                                        .collect(Collectors.toList());


    public MRTRoutePlannerAbility(BaseAbilityBot extensionUser) { this.extensionUser = extensionUser; }

    public Ability go() {
        /*
        fuzzy string matching to select 2 stations. Return a list of strings and send it back to the client
         */
        Station[] selectedStations = {null};
        final String askForDestinationStation = "Hi! Which station do you wanna go?";
        final String askForStartingStation = "Ok. From which station?";
        return Ability.builder()
                .name("go")
                .info("Plans an MRT route, telling you which door to alight for maximum travel efficiency")
                .input(0)
                .privacy(PUBLIC)
                .locality(ALL)
                .action(ctx -> {
                    extensionUser.silent().forceReply(askForDestinationStation, ctx.chatId());
                })
                .reply(ReplyFlow.builder(this.extensionUser.db(),1)
                        .action((bot, upd) -> {
                            String selectedStn = upd.getMessage().getText();
                            Station s1 = bestMatchByLevenshteinDistance(selectedStn,
                                    Stations.getAllStations(),
                                    stn -> stn.getName()
                            );
                            System.out.println("User selected :" + s1);
                            selectedStations[0] = s1;
                            extensionUser.silent().send("Noted. " + s1.getName() + " it is.", getChatId(upd));
                            extensionUser.silent().forceReply(askForStartingStation, getChatId(upd));
                        })
                        .onlyIf(isReplyToMessage(askForDestinationStation))
                        .next(
                                Reply.of(
                                        (bot, upd) -> {
                                            String selectedStn = upd.getMessage().getText();
                                            Station s2 = bestMatchByLevenshteinDistance(selectedStn, Stations.getAllStations(), stn -> stn.getName());
                                            List<String> instructions = planner.plan(s2, selectedStations[0]);
                                            String msg = instructions.stream().reduce((line1, line2) -> line1 + "\n" + line2).get();
                                            extensionUser.silent().send(msg, upd.getMessage().getChatId());
                                        },
                                        MESSAGE,
                                        REPLY,
                                        isReplyToMessage(askForStartingStation)
                                )
                        )
                        .build()
                )
                .build();
    }
    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    public Ability alight() {
        final int[] state = {0};
        return Ability.builder()
                .name("station")
                .info("Shows you the nearest train exits to the places of interest around the station")
                .input(1)
                .privacy(PUBLIC)
                .locality(ALL)
                .action(ctx -> {
                    System.out.println(ctx.arguments().length + " args");
                    System.out.println(Arrays.toString(ctx.arguments()));
                    String selectedStnStr = ctx.arguments()[0];
                    Station s1 = bestMatchByLevenshteinDistance(selectedStnStr, Stations.getAllStations(), stn -> stn.getName());
                    Map<Line, List<Integer>> transitions = s1.getTransitions();
                    Optional<Map<String, List<Integer>>> placesOfInterest = s1.getPlacesOfInterest();

                    StringBuilder sb = new StringBuilder();
                    sb.append(s1.getName() + "\n");
                    for (Line l : transitions.keySet()) {
                        sb.append("Towards " + l.toString());
                        sb.append(": doors " + formatListOfChoices(transitions.get(l)));
                        sb.append("\n");
                    }
                    if (placesOfInterest.isPresent()) {
                        for (String l : placesOfInterest.get().keySet()) {
                            sb.append(l);
                            sb.append(": doors " + formatListOfChoices(placesOfInterest.get().get(l)));
                            sb.append("\n");
                        }
                    }

                    extensionUser.silent().send(sb.toString(), ctx.chatId());
                })
                .build();
    }
    private static int indexToCutTheList(String s) {
        int len = s.length();
        int bestIdx = 0;
        int currentScore = Integer.MIN_VALUE;

        for (int i = 0; i < len; i++) {
            String from = s.substring(0, i);
            String to = s.substring(i, s.length());
            int score = bestScoreByLevenshteinDistance(from, stations) + bestScoreByLevenshteinDistance(to, stations);
            if (score > currentScore) {
                currentScore = score;
                bestIdx = i;
            }
        }
        return bestIdx;
    }
}