package sg.mrt_navigation.planner;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import sg.mrt_navigation.JSONMRTNetworkBuilder;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.domain.Stations;
import sg.utils.JSONValidator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static sg.mrt_navigation.planner.TrainRoutePlannerTest.buildTrainNetwork;

public class JSONMRTNetworkBuilderTest {

    @Test
    public void initialize() {
        new JSONMRTNetworkBuilder();
    }

    @Test
    public void buildAGraph() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        b.build();
    }

    @Test
    public void checkTransitionsJSONForMistakes() throws URISyntaxException, IOException {
        buildTrainNetwork();

        URI transitionsList = JSONMRTNetworkBuilderTest.class.getResource("/json-network-data/transitions.json").toURI();
        URI transitionsSchema = JSONMRTNetworkBuilderTest.class.getResource("/transitions-schema.json").toURI();
        JSONValidator jsonValidator = new JSONValidator(transitionsSchema);
        JsonNode transitionsListJson = JSONValidator.parse(Files.readString(Path.of(transitionsList)));
        JSONValidator.Result result = jsonValidator.checkForValidity(transitionsListJson);
        assertTrue(result.isValid());

        for (JsonNode transition : transitionsListJson) {
            JsonNode stations = transition.get("stations");
            String startingStnCodeStr = stations.get(0).asText();
            String endingStnCodeStr = stations.get(1).asText();
            String[] startingStnCodeArray = startingStnCodeStr.split(":");
            String[] endingStnCodeArray = endingStnCodeStr.split(":");


            Station startingStation = Stations.getOrError(
                    Integer.parseInt(startingStnCodeArray[1]),
                    Line.valueOf(startingStnCodeArray[0])
            );
            Station endingStation = Stations.getOrError(
                    Integer.parseInt(endingStnCodeArray[1]),
                    Line.valueOf(endingStnCodeArray[0])
            );
            if (
                    (startingStation.getStationCode().getLine() == Line.SE && endingStation.getStationCode().getLine() == Line.SE) ||
                    (startingStation.getStationCode().getLine() == Line.SW && endingStation.getStationCode().getLine() == Line.SW) ||
                    (startingStation.getStationCode().getLine() == Line.PE && endingStation.getStationCode().getLine() == Line.PE) ||
                    (startingStation.getStationCode().getLine() == Line.PW && endingStation.getStationCode().getLine() == Line.PW)

            ) {
                int numStns = Stations.getAllStationsFromLine(startingStation.getStationCode().getLine()).size();
                if (
                        (startingStation.getStationCode().getId() == 0 && endingStation.getStationCode().getId() == numStns - 1) ||
                        (startingStation.getStationCode().getId() == numStns - 1 && endingStation.getStationCode().getId() == 0)
                ) {
                    continue;
                }
            } else if (
                    startingStation.getStationCode().getLine() == Line.BP
                    && endingStation.getStationCode().getLine() == Line.BP
                    && (
                            (startingStation.getStationCode().getId() == 6
                            && endingStation.getStationCode().getId() == 13)  ||
                            (startingStation.getStationCode().getId() == 13
                            && endingStation.getStationCode().getId() == 6)
                    )

            ) {
                continue;
            }
            assertEquals(startingStnCodeStr + " -> " + endingStnCodeStr,startingStation.getName(), endingStation.getName());
        }

    }


}