package sg.mrt_navigation.planner;

import sg.mrt_navigation.domain.Line;
import sg.mrt_navigation.domain.Station;

import java.util.List;

public class TrainRouteSegment {
    List<Station> stations;
    TrainRouteSegment(List<Station> stations) {
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

    public int size() {
        return stations.size();
    }
    @Override
    public String toString() {
        return "[" + getStartingStation() + " -> " + getEndingStation() + "]";
    }
}