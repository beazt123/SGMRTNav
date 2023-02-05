package sg.mrt_navigation.planner;

import org.jgrapht.graph.DefaultEdge;
import sg.mrt_navigation.domain.Station;
import sg.mrt_navigation.network.Network;

public final class MRTNetwork {
    public static final Network<Station, DefaultEdge> mrtNetwork = new JSONMRTNetworkBuilder().build();
}
