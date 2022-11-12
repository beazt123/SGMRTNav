package sg.mrt_navigation.domain;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class StationCode implements Comparable<StationCode> {
    private int id;

    private Line line;
    public static final Map<Integer, StationCode> stationCodes = new HashMap<>();
    private StationCode(int id, Line line) {
        this.id = id;
        this.line = line;
    }

    public int getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public static StationCode getStationCode(int id, Line line) {
        int hash = calculateHashCode(id, line);
        if (!stationCodes.containsKey(hash)) {
            stationCodes.put(hash, new StationCode(id, line));
        }
        return stationCodes.get(hash);
    }

    @Override
    public int compareTo(@NotNull StationCode stationCode) {
        if (line.equals(stationCode.line))
            if (id > stationCode.id) return 1;
            else if (id < stationCode.id) return -1;
            else return 0;
        else throw new IllegalArgumentException("Stations of different lines cannot be compared");
    }

    @Override
    public int hashCode() {
        return calculateHashCode(id, line);
    }

    private static int calculateHashCode(int id, Line line) {
        int result = id;
        result = 37 * result + line.ordinal() + 1;
        return result;
    }
}
