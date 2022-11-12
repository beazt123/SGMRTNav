package sg.mrt_navigation.network;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

public class Network<V,E> {
    private Graph<V,E> graph;

    public Network() {}

    public Graph<V, E> getGraph() {
        return graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = graph;
    }

    public Network<V,E> addEdge(V vertice1, V vertice2, double weight) {
        E e = graph.addEdge(vertice1, vertice2);
        graph.setEdgeWeight(e, weight);
        return this;
    }
    public Network<V,E> addVertex(V vertex) {
        graph.addVertex(vertex);
        return this;
    }
}
