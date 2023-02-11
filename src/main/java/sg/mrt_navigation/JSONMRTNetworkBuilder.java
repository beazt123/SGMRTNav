package sg.mrt_navigation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.domain.Stations;
import sg.mrt_navigation.network.Network;
import sg.mrt_navigation.network.NetworkBuilder;
import sg.utils.JSONValidator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONMRTNetworkBuilder implements NetworkBuilder<Station, DefaultEdge> {
    private static JSONValidator stationsListJSONvalidator;
    private static JSONValidator transitionsJSONvalidator;
    private JsonNode transitionsList;

    static {
        try {
            stationsListJSONvalidator = new JSONValidator(JSONMRTNetworkBuilder.class.getResource("/json-schemas/station-list-schema.json").toURI());
            transitionsJSONvalidator = new JSONValidator(JSONMRTNetworkBuilder.class.getResource("/json-schemas/transitions-schema.json").toURI());
        } catch (URISyntaxException e) {
            // Will not happen during runtime as the developer configures the class
        }
    }

    public JSONMRTNetworkBuilder(Set<File> stationsListFiles, File transitionsFile) {
        parseJsonFilesAndFlyweightStations(stationsListFiles);
        transitionsList = parseTransitionsList(transitionsFile);
    }
    public JSONMRTNetworkBuilder() {
        File jsonDataFolder = null;
        File transitionsFile = null;
        try {
            jsonDataFolder = new File(JSONMRTNetworkBuilder.class.getResource("/json-network-data").toURI());
            transitionsFile = new File(JSONMRTNetworkBuilder.class.getResource("/json-network-data/transitions.json").toURI());
        } catch (URISyntaxException e) {
            // Won't happen since the developer configures this class
        }
        Set<File> stationsListFiles = Stream.of(jsonDataFolder.listFiles())
                .filter(file -> !(file.isDirectory() || file.getName().equals("transitions.json")) )
                .map(File::getAbsoluteFile)
                .collect(Collectors.toSet());

        parseJsonFilesAndFlyweightStations(stationsListFiles);
        transitionsList = parseTransitionsList(transitionsFile);
    }

    private void parseJsonFilesAndFlyweightStations(Set<File> listFiles) {
        for (File path : listFiles) {
            try {
                parseFileAndCollectStations(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseFileAndCollectStations(File path) throws IOException {
        JsonNode jsonObj = parseFileIntoJson(path);
        JSONValidator.Result result = stationsListJSONvalidator.checkForValidity(jsonObj);
        if (!result.isValid()) {
            throw new IllegalStateException("JSON data structure in File\n\t"
                    + path.toString() + "\n"
                    + "does not match specifications at\n\t"
                    + result.getSchemaPath() +"\n"
                    + "Details:\n"
                    + result.getMsgs());
        }
        // actually making it into part of the network
        for (JsonNode station : jsonObj) {
            deserializeToStation(station);
        }
    }

    private static void deserializeToStation(JsonNode station) {
        String stationName = station.get("name").asText();
        int stationId = station.get("id").asInt();
        Line stationLine = Line.valueOf(station.get("line").asText());


        Map<Line, List<Integer>> transitionsMap = new HashMap<>();
        Map<String, List<Integer>> placesOfInterest = new HashMap<>();
        JsonNode transitions = station.get("transitions");
        if (transitions != null) {
            for (int i = 0; i < transitions.size(); i++) {
                JsonNode transition = transitions.get(i);
                String to = transition.get("to").asText();
                List<Integer> exits = new ObjectMapper().convertValue(transition.get("exits"), ArrayList.class);

                try {
                    Line l = Line.valueOf(to.toUpperCase());
                    transitionsMap.put(l, exits);
                } catch (IllegalArgumentException e) {
                    placesOfInterest.put(to, exits);
                }
            }
        }

        JsonNode exits = station.get("exits");
        List<Integer> e = new ObjectMapper().convertValue(exits, ArrayList.class);
        transitionsMap.put(Line.EXIT, e);

        Stations.create(stationId, stationLine, stationName, transitionsMap, placesOfInterest);
    }

    private static JsonNode parseFileIntoJson(File path) throws IOException {
        String jsonData = Files.readString(Path.of(path.toURI()));
        return JSONValidator.parse(jsonData);
    }

    private JsonNode parseTransitionsList(File transitionsList) {
        String jsonData = null;
        try {
            jsonData = Files.readString(Path.of(transitionsList.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonNode jsonObj = JSONValidator.parse(jsonData);
        JSONValidator.Result result = transitionsJSONvalidator.checkForValidity(jsonObj);
        if (!result.isValid()) {
            throw new IllegalStateException("Transition JSON data does not match the specs: " + result.getMsgs().toString());
        } else {
            return jsonObj;
        }
    }

    private static Station parseStrStationCode(String str) {
        /*
        preconditions: The stations have all been created
         */
        String[] stationCodeParts = str.split(":");
        Line l = Line.valueOf(stationCodeParts[0]);
        int id = Integer.valueOf(stationCodeParts[1]);
        return Stations.getOrError(id, l);
    }

    private static Network<Station, DefaultEdge> addIndividualMRTLines(Network<Station, DefaultEdge> mrtNetwork) {
        for (Line line : Line.values()){
            List<Station> lineStations = Stations.getAllStationsFromLine(line);

            lineStations.stream()
                    .map(stn -> {
                        mrtNetwork.addVertex(stn);
                        return stn;
                    })
                    .reduce((stn1, stn2) -> {
                        mrtNetwork.addEdge(stn1, stn2, line.getAverageTimeBetweenStns());
                        return stn2;
                    });
        }
        return mrtNetwork;
    }

    @Override
    public Network<Station, DefaultEdge> build() {

        Network<Station, DefaultEdge> mrtNetwork = new Network<>();
        mrtNetwork.setGraph(new DefaultUndirectedWeightedGraph<>(DefaultEdge.class));
        mrtNetwork = addIndividualMRTLines(mrtNetwork);

        for (JsonNode transition : transitionsList) {
            JsonNode stations = transition.get("stations");
            double walkingTime = transition.get("walking_time").asDouble();
            Station station1 = parseStrStationCode(stations.get(0).asText());
            Station station2 = parseStrStationCode(stations.get(1).asText());

            mrtNetwork.addEdge(station1, station2, walkingTime);
        }

        return mrtNetwork;
    }
}
