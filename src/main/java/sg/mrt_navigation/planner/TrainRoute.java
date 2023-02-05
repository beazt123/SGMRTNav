package sg.mrt_navigation.planner;

import com.google.common.collect.ImmutableList;
import sg.mrt_navigation.domain.Station;

import java.util.ArrayList;
import java.util.List;

import static sg.utils.UserInputProcessing.levenshteinDistance;

public class TrainRoute {
    List<TrainRouteSegment> segments;

    TrainRoute(List<Station> route) {
        List<TrainRouteSegment> segmentsRaw = splitIntoSegmentsByTrainLine(route);
        this.segments = removeIrrevelantTransitions(segmentsRaw);
    }

    public List<TrainRouteSegment> getSegments() {
        return ImmutableList.copyOf(segments);
    }

    private static List<TrainRouteSegment> splitIntoSegmentsByTrainLine(List<Station> route) {
        List<TrainRouteSegment> ls = new ArrayList<>();
        List<Integer> transitionIndices = findTransitionIndices(route);
        transitionIndices.add(route.size() - 1);

        transitionIndices.stream()
                .reduce(0,(idx1, idx2) -> {
                    ls.add(new TrainRouteSegment(route.subList(idx1, idx2 + 1)));
                    return idx2 + 1;
                });
        return ls;
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

    private static List<TrainRouteSegment> removeIrrevelantTransitions(List<TrainRouteSegment> segments) {
        /*
        This is to fix the problem of same station transitions which the algorithm will ask the passenger to do
         */
        List<TrainRouteSegment> outputList = new ArrayList<>();
        for (TrainRouteSegment segment: segments) {
            if (segment.size() == 1) {
                continue;
            } else if (segment.size() == 2) {
                Station s1 = segment.getStartingStation();
                Station s2 = segment.getEndingStation();

                if (levenshteinDistance(s1.getName(), s2.getName()) > 95) {
                    continue;
                }
            }
            outputList.add(segment);
        }
        return outputList;
    }
}
