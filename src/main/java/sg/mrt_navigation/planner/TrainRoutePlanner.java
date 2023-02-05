package sg.mrt_navigation.planner;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.DefaultEdge;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.network.Network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sg.utils.UserInputProcessing.formatListOfChoices;


public class TrainRoutePlanner {
    Network<Station, DefaultEdge> network;
    ShortestPathAlgorithm<Station, DefaultEdge> algo;

    public TrainRoutePlanner(Network network) {
        this.network = network;
        this.algo = new BFSShortestPath<Station, DefaultEdge>(network.getGraph());
    }

    public List<Station> getRoute(Station start, Station end) {
        return algo.getPath(start, end).getVertexList();
    }

    public List<String> plan(Station start, Station end) {
        TrainRoute route = new TrainRoute(getRoute(start, end));
        List<TrainRouteSegment> lineSegments = route.getSegments();
        return convertToHumanReadableInstructions(lineSegments);
    }

    static List<String> convertToHumanReadableInstructions(List<TrainRouteSegment> route) {
        /*
        Board Sengkang station at door X, alight at Serangoon
        Board Serangoon(CCL) stations at door Y, alight at Dhoby Ghaut

        Board STARTING_STN station at SEGMENT.GETENDINGSTN().TRANSITTO(DIRECTION, NEXT_LINE).
         */
        List<String> instructions = new ArrayList<>();
        route.stream()
                .reduce((segment1, segment2) -> {
                    int direction = segment1.getDirection();
                    List<Integer> doorsToAlightFrom = segment1.getEndingStation().transitTo(
                            segment2.getStartingStation().getStationCode().getLine(),
                            direction
                    );
                    String instruction = formatInstruction(segment1.getStartingStation(),
                            segment1.getEndingStation(),
                            doorsToAlightFrom
                    );
                    instructions.add(instruction);
                    return segment2;
                });
        TrainRouteSegment lastSegment = route.get(route.size() - 1);

        List<Integer> exitDoor = lastSegment.getEndingStation().transitTo(Line.EXIT, lastSegment.getDirection());
        instructions.add(formatInstruction(lastSegment.getStartingStation(),
                lastSegment.getEndingStation(),
                exitDoor));

        return instructions;
    }

    private static String formatInstruction(Station start, Station stop, List<Integer> alightDoor) {
        StringBuilder sb = new StringBuilder("Board at ");
        sb.append(start.getName())
            .append(" at door");

        if (alightDoor.size() > 1) {
            sb.append("s");
        }

        sb.append(" ").append(formatListOfChoices(alightDoor));

        sb.append(", alight at ")
            .append(stop.getName());

        return sb.toString();
    }




}
