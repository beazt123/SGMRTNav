package sg.mrt_navigation.planner;

import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.domain.Stations;
import sg.mrt_navigation.network.Network;

import java.util.List;

import static org.junit.Assert.*;

public class TrainRoutePlannerTest {
    @Test
    public void findShortTestPathSameLine() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        Network<Station, DefaultEdge> network = b.build();
        TrainRoutePlanner planner = new TrainRoutePlanner(network);
        Station start = Stations.getOrError(17, Line.NE);
        Station end = Stations.getOrError(7, Line.NE);
//        System.out.println(start);
//        System.out.println(end);
        List<Station> path = planner.getRoute(start, end);
//        System.out.println(path);
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
        Station start = Stations.getOrError(1, Line.TE);
        Station end = Stations.getOrError(7, Line.PE);
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        List<Station> path = planner.getRoute(start, end);
        path.stream().forEach(stn -> System.out.println(stn));
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
        path.stream().forEach(stn -> System.out.println(stn));
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
        System.out.println(TrainRoutePlanner.splitIntoSegmentsByTrainLine(trainRoutePlanner.getRoute(start, end)));
    }
}