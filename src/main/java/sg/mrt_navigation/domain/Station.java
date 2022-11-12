package sg.mrt_navigation.domain;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Station implements Comparable<Station> {
    private StationCode stationCode;
    private String name;
    private Map<Line, Integer> transitions;

    Station(StationCode stationCode,
            String name,
            Map<Line, Integer> transitions) {
        this.stationCode = stationCode;
        this.transitions = transitions;
        this.name = name;
    }

    Station(int id,
            Line line,
            String name,
            Map<Line, Integer> transitions) {
        this(StationCode.getStationCode(id, line), name, transitions);
    }


    public StationCode getStationCode() {
        return stationCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull Station station) {
        return stationCode.compareTo(station.getStationCode());
    }

    public int transitTo(Line line, int direction) {
        // returns the door to exit given the default direction is increasing order of station number within the same line
        int doorToExitFrom = transitions.get(line);
        if (direction < 0) {
            doorToExitFrom = stationCode.getLine().getNumDoors() - doorToExitFrom;
        }
        return doorToExitFrom;
    }

    @Override
    public String toString() {
        return name + "(" + stationCode.getLine() + ":" + stationCode.getId() + ")";
    }

}


