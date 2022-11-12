package sg.mrt_navigation.planner;

import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.DefaultEdge;
import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.network.Network;

import java.util.ArrayList;
import java.util.List;


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
        List<Station> route = getRoute(start, end);
        List<Segment> lineSegments = splitIntoSegmentsByTrainLine(route);
        return convertToHumanReadableInstructions(lineSegments);
    }

    static List<String> convertToHumanReadableInstructions(List<Segment> route) {
        /*
        Board Sengkang station at door X, alight at Serangoon
        Board Serangoon(CCL) stations at door Y, alight at Dhoby Ghaut

        Board STARTING_STN station at SEGMENT.GETENDINGSTN().TRANSITTO(DIRECTION, NEXT_LINE).
         */
        List<String> instructions = new ArrayList<>();
        route.stream()
                .reduce((segment1, segment2) -> {
                    int direction = segment1.getDirection();
                    int doorToAlightFrom = segment1.getEndingStation().transitTo(
                            segment2.getStartingStation().getStationCode().getLine(),
                            direction
                    );
                    String instruction = formatInstruction(segment1.getStartingStation(),
                            segment1.getEndingStation(),
                            doorToAlightFrom
                    );
                    instructions.add(instruction);
                    return segment2;
                });
        Segment lastSegment = route.get(route.size() - 1);

        int exitDoor = lastSegment.getEndingStation().transitTo(Line.EXIT, lastSegment.getDirection());
        instructions.add(formatInstruction(lastSegment.getStartingStation(),
                lastSegment.getEndingStation(),
                exitDoor));

        return instructions;
    }

    private static String formatInstruction(Station start, Station stop, int alightDoor) {
        return "Board at " + start.getName() + " at door " + alightDoor + ", alight at " + stop.getName();
    }

    private static List<Integer> findTransitionIndices(List<Station> route) {
        List<Integer> indices = new ArrayList<>();
        route.stream()
                .reduce((stn1, stn2) -> {
//                    if (stn2 == null) return stn1;
                    if (stn1.getStationCode().getLine() != stn2.getStationCode().getLine()) {
                        indices.add(route.indexOf(stn1));
                    }
                    return stn2;
                });
        return indices;
    }

    static List<Segment> splitIntoSegmentsByTrainLine(List<Station> route) {
        List<Segment> ls = new ArrayList<>();
        List<Integer> transitionIndices = findTransitionIndices(route);
        transitionIndices.add(route.size() - 1);

        transitionIndices.stream()
                .reduce(0,(idx1, idx2) -> {
                    ls.add(new Segment(route.subList(idx1, idx2 + 1)));
                    return idx2 + 1;
                });
        return ls;
    }

    private static class Segment {
        List<Station> stations;
        Segment(List<Station> stations) {
            this.stations = stations;
        }
        Station getStartingStation() {
            return stations.get(0);
        }
        Station getEndingStation() {
            return stations.get(stations.size() - 1);
        }
        Line getLine() {
            /*
            Precondition: All stations in a segment are from the same line
             */
            return stations.get(0).getStationCode().getLine();
        }
        int getDirection() {
            return getEndingStation().compareTo(getStartingStation());
        }
        @Override
        public String toString() {
            return "[" + getStartingStation() + " -> " + getEndingStation() + "]";
        }
    }
}
