package sg.mrt_navigation.planner;

import com.fasterxml.jackson.databind.JsonNode;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.domain.Stations;
import sg.mrt_navigation.network.Network;
import sg.mrt_navigation.network.NetworkBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONMRTNetworkBuilder implements NetworkBuilder<Station, DefaultEdge> {
    private JSONValidator stationsListJSONvalidator;
    private JSONValidator transitionsJSONvalidator;
    private URL jsonDataFolder;

    public JSONMRTNetworkBuilder() {
        this(JSONMRTNetworkBuilder.class.getResource("/json-network-data"),
            JSONMRTNetworkBuilder.class.getResource("/json-schemas/transitions-schema.json"),
            JSONMRTNetworkBuilder.class.getResource("/json-schemas/station-list-schema.json"));

    }

    public JSONMRTNetworkBuilder(URL jsonDataFolder,
                                 URL transitionsJsonSchema,
                                 URL stationsListJsonSchema) {
        this.jsonDataFolder = jsonDataFolder;
        try {
            this.stationsListJSONvalidator = new JSONValidator(stationsListJsonSchema.toURI());
            this.transitionsJSONvalidator = new JSONValidator(transitionsJsonSchema.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void parseStationsList() {
        try {
            Set<File> listFiles = Stream.of(new File(jsonDataFolder.toURI()).listFiles())
                    .filter(file -> !(file.isDirectory() || file.getName().equals("transitions.json")) )
                    .map(File::getAbsoluteFile)
                    .collect(Collectors.toSet());

            for (File path : listFiles) {
                try {
                    parseFileAndCollectStations(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void parseFileAndCollectStations(File path) throws IOException {
        JsonNode jsonObj = parseFileIntoJson(path);
        if (!stationsListJSONvalidator.validateStationList(jsonObj)) {
            throw new IllegalStateException("JSON data could have been outdated");
        }
        // actually making it into part of the network
        for (JsonNode station : jsonObj) {
            deserialiseToStation(station);
        }
    }

    private static void deserialiseToStation(JsonNode station) {
        String stationName = station.get("name").asText();
        int stationId = station.get("id").asInt();
        Line stationLine = Line.valueOf(station.get("line").asText());

        JsonNode transitions = station.get("transitions");
        Map<Line, Integer> transitionsMap = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = transitions.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            String lineCode = entry.getKey();
            transitionsMap.put(Line.valueOf(lineCode.toUpperCase()), entry.getValue().asInt());
        }
        Stations.create(stationId, stationLine, stationName, transitionsMap);
    }

    private static JsonNode parseFileIntoJson(File path) throws IOException {
        String jsonData = Files.readString(Path.of(path.toURI()));
        return JSONValidator.parse(jsonData);
    }

    private JsonNode parseTransitionsList(URL transitionsURL) {
        String jsonData = null;
        try {
            jsonData = Files.readString(Path.of(transitionsURL.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JsonNode jsonObj = JSONValidator.parse(jsonData);
        if (!transitionsJSONvalidator.validateStationList(jsonObj)) {
            throw new IllegalStateException("Transition JSON data could have been outdated");
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
        parseStationsList();
        JsonNode transitionsList = parseTransitionsList(JSONMRTNetworkBuilder.class.getResource("/json-network-data/transitions.json"));

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
