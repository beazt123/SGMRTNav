package sg.mrt_navigation.network;

import org.jgrapht.graph.DefaultEdge;
import sg.mrt_navigation.domain.Station;

public interface NetworkBuilder<V,E> {
    Network<Station, DefaultEdge> build();
}
