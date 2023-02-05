package sg.mrt_navigation.domain;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class Stations {
//    private static final SortedMap<StationCode, Station> stations = new TreeMap<>();
    private static final Map<Line, SortedMap<StationCode, Station>> stations = new HashMap<>();
    static {
        for (Line l : Line.values()) {
            stations.put(l, new TreeMap());
        }
    }

    public static Station create(int id,
                                Line line,
                                String name,
                                Map<Line, List<Integer>> transitions) {
        /*
        does not return the original station you intend to create if there is already a flyweighted Station with the same id & line
         */
        return create(id, line, name, transitions, null);
    }

    public static Station create(int id,
                                Line line,
                                String name,
                                Map<Line, List<Integer>> transitions,
                                Map<String, List<Integer>> placesOfInterest) {
        /*
        does not return the original station you intend to create if there is already a flyweighted Station with the same id & line
         */
        StationCode stationCode = StationCode.getStationCode(id, line);
        Map<StationCode, Station> existingStations = stations.get(line);
        if (existingStations.containsKey(stationCode)) {
            return existingStations.get(stationCode);
        } else {
            Station instance = new Station(id, line, name, transitions, placesOfInterest);
            existingStations.put(stationCode, instance);
            return instance;
        }
    }

    public static Optional<Station> get(StationCode stationCode) {
        Line l = stationCode.getLine();
        SortedMap<StationCode, Station> existingStations = stations.get(l);
        return Optional.ofNullable(existingStations.get(stationCode));
    }

    public static Optional<Station> get(int id, Line line) {
        StationCode code = StationCode.getStationCode(id, line);
        return get(code);
    }

    public static Station getOrError(int id, Line line) {
        boolean exists = checkIfExists(id, line);
        if (exists) return get(id, line).get();
        else {
            throw new NoSuchElementException("The requested " + Station.class.getName() + "(ID: " + id + ", Line: " + line + ") does not exist");
        }
    }

    public static boolean checkIfExists(int id, Line line) {
        return get(id, line).isPresent();
    }

    public static List<Station> getAllStationsFromLine(Line l) {
        ImmutableList<Station> selectedStations = ImmutableList.copyOf(stations.get(l).values());
        return selectedStations;
    }

    public static List<Station> getAllStations() {
        List<Station> ls = new ArrayList<>();
        for (Line l : stations.keySet())
            ls.addAll(stations.get(l).values());
        return ImmutableList.copyOf(ls);
    }
}
