package sg.mrt_navigation.planner;

import org.jetbrains.annotations.NotNull;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import sg.mrt_navigation.JSONMRTNetworkBuilder;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;

import sg.mrt_navigation.domain.Stations;
import sg.mrt_navigation.network.Network;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrainRoutePlannerTest {

    @Test
    public void inspectAllStations() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        Stations.getAllStations().stream().forEach(System.out::println);
    }

    @Test
    public void findShortTestPathSameLine() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner planner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(2, Line.CE);
        Station end = Stations.getOrError(16, Line.NE);
        List<Station> path = planner.getRoute(start, end);
        path.stream().forEach(System.out::println);
    }

    @Test
    public void findShortTestPathDiffLine() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner planner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(17, Line.EW);
        Station end = Stations.getOrError(7, Line.NE);
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        List<Station> path = planner.getRoute(start, end);
        System.out.println(path);
    }

    @Test
    public void findShortTestPathDiffStrangeLines() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner planner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(2, Line.CE);
        Station end = Stations.getOrError(16, Line.NE);
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        List<Station> path = planner.getRoute(start, end);
        TrainRoute tr = new TrainRoute(path);
        tr.getSegments().stream().forEach(stn -> System.out.println(stn));
    }
    @Test
    public void findShortTestPathDiffLRTLines() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner planner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(7, Line.PE);
        Station end = Stations.getOrError(0, Line.PE);
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        List<Station> path = planner.getRoute(start, end);
        new TrainRoute(path).getSegments().stream().forEach(stn -> System.out.println(stn));
    }

    @Test
    public void getRouteInstructions() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner trainRoutePlanner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(1, Line.DT);
        Station end = Stations.getOrError(2, Line.CG);
        trainRoutePlanner.plan(start, end)
                .stream()
                .forEach(System.out::println);
    }

    @Test
    public void splitIntoSegmentsTest() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner trainRoutePlanner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(7, Line.TE);
        Station end = Stations.getOrError(1, Line.NS);
        trainRoutePlanner.getRoute(start, end)
                .stream()
                .forEach(System.out::println);
//        System.out.println(TrainRoutePlanner.splitIntoSegmentsByTrainLine(trainRoutePlanner.getRoute(start, end)));
        trainRoutePlanner.plan(start, end)
                .stream()
                .forEach(System.out::println);
    }

    @Test
    public void testAllPossibleCombinations() throws URISyntaxException {
        /*
        This test case tests for error if Outram park was mapped to Ang Mo Kio in SG MRT Map
         */
        TrainRoutePlanner planner = new TrainRoutePlanner(buildTrainNetwork());

        List<Station> stations = Stations.getAllStations();
        List<int[]> journeys = generate(stations.size(), 2);
        for (int[] journey : journeys) {
            int startingStationIdx = journey[0];
            int endingStationIdx = journey[1];
            Station startingStation = stations.get(startingStationIdx);
            Station endingStation = stations.get(endingStationIdx);
            List<Station> route = planner.getRoute(startingStation, endingStation);
            assertEquals(route.get(0), startingStation);
            assertEquals(route.get(route.size() - 1), endingStation);

        }



    }

    @NotNull
    public static Network<Station, DefaultEdge> buildTrainNetwork() {
        File transitionsFile;
        File jsonDataFolder;
        try {
            transitionsFile = new File(JSONMRTNetworkBuilder.class.getResource("/json-network-data/transitions.json").toURI());
            jsonDataFolder = new File(JSONMRTNetworkBuilder.class.getResource("/json-network-data").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Set<File> stationsListFiles = Stream.of(jsonDataFolder.listFiles())
                .filter(file -> !(file.isDirectory() || file.getName().equals("transitions.json")) )
                .map(File::getAbsoluteFile)
                .collect(Collectors.toSet());
        return new JSONMRTNetworkBuilder(stationsListFiles, transitionsFile).build();


    }

    public static List<int[]> generate(int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        helper(combinations, new int[r], 0, n-1, 0);
        return combinations;
    }

    public static void helper(List<int[]> combinations, int data[], int start, int end, int index) {
        if (index == data.length) {
            int[] combination = data.clone();
            combinations.add(combination);
        } else if (start <= end) {
            data[index] = start;
            helper(combinations, data, start + 1, end, index + 1);
            helper(combinations, data, start + 1, end, index);
        }
    }


}