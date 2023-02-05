package sg.mrt_navigation.domain;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Station implements Comparable<Station> {
    private StationCode stationCode;
    private String name;


    private Map<Line, List<Integer>> transitions;

    private Map<String, List<Integer>> placesOfInterest;


    Station(StationCode stationCode,
            String name,
            Map<Line, List<Integer>> transitions) {
        this.stationCode = stationCode;
        this.transitions = transitions;
        this.name = name;
    }

    Station(int id,
            Line line,
            String name,
            Map<Line, List<Integer>> transitions) {
        this(StationCode.getStationCode(id, line), name, transitions);
    }

    Station(StationCode stationCode,
            String name,
            Map<Line, List<Integer>> transitions,
            Map<String, List<Integer>> placesOfInterest) {
        this(stationCode, name, transitions);
        this.placesOfInterest = placesOfInterest;
    }

    Station(int id,
            Line line,
            String name,
            Map<Line, List<Integer>> transitions,
            Map<String, List<Integer>> placesOfInterest) {
        this(StationCode.getStationCode(id, line), name, transitions);
        this.placesOfInterest = placesOfInterest;
    }

    public Optional<Map<String, List<Integer>>> getPlacesOfInterest() {
        return Optional.ofNullable(placesOfInterest);
    }

    public Map<Line, List<Integer>> getTransitions() {
        Map<Line, List<Integer>> h = new HashMap<>();
        for (Line l : transitions.keySet()) {
            h.put(l, ImmutableList.copyOf(transitions.get(l)));
        }
        return h;
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

    public List<Integer> transitTo(Line line, int direction) {
        // returns the door to exit given the default direction is increasing order of station number within the same line
        final List<Integer> finalDoorsToExitFrom = new ArrayList<>();
        final List<Integer> provisionalExitDoors = transitions.get(line);
        if (direction < 0) {
            for (int i = 0; i < provisionalExitDoors.size(); i++){
                finalDoorsToExitFrom.add(stationCode.getLine().getNumDoors() - provisionalExitDoors.get(i));
            }
            Collections.sort(finalDoorsToExitFrom);
            return  ImmutableList.copyOf(finalDoorsToExitFrom);
        }
        Collections.sort(provisionalExitDoors);
        return ImmutableList.copyOf(provisionalExitDoors);
    }

    @Override
    public String toString() {
        return name + "[" + stationCode.getLine() + stationCode.getId() + "," + "doors: " + transitions.toString() + "]";
    }

}


