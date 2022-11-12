package sg.navbot;

import org.jgrapht.GraphPath;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.junit.Test;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import java.io.FileWriter;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
//import org.jgrapht.nio.*;
//import org.jgrapht.nio.dot.*;
//import org.jgrapht.traverse.*;

//import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.List;
//import java.util.*;

public class JGraphtLearningTest {
	@Test
	public void creatingAGraphWithArbitraryObjects() {
		Graph<URI, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

		URI google = null;
		URI wikipedia = null;
		URI jgrapht = null;
		try {
			google = new URI("http://www.google.com");
			wikipedia = new URI("http://www.wikipedia.org");
			jgrapht = new URI("http://www.jgrapht.org");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// add the vertices
		g.addVertex(google);
		g.addVertex(wikipedia);
		g.addVertex(jgrapht);

		// add edges to create linking structure
		g.addEdge(jgrapht, wikipedia);
		g.addEdge(google, jgrapht);
		g.addEdge(google, wikipedia);
		g.addEdge(wikipedia, google);
	}

	@Test
	public void exportingAGraphToCsv() throws IOException {
		Graph<String, DefaultEdge> g = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
		g.addVertex("SengKang");
		g.addVertex("Buangkok");
		g.addVertex("Hougang");
		g.addEdge("SengKang", "Buangkok");
		g.addEdge("Hougang", "Buangkok");
		CSVExporter<String, DefaultEdge> ce = new CSVExporter(CSVFormat.MATRIX, ',');
		ce.exportGraph(g, new FileWriter("D:\\Desktop\\External-Commitments\\Projects\\SGMRTNavBot\\WAKANDA_FOREVER.csv"));
	}

	@Test
	public void shortestPathFor3Nodes() {
		Graph<String, DefaultEdge> g = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
		g.addVertex("SengKang");
		g.addVertex("Buangkok");
		g.addVertex("Hougang");

		g.addEdge("SengKang", "Buangkok");
		g.addEdge("Hougang", "Buangkok");
		BFSShortestPath<String, DefaultEdge> bfs = new BFSShortestPath<>(g);
		GraphPath<String, DefaultEdge> path = bfs.getPath("SengKang", "Hougang");
		List<String> stationList = path.getVertexList();
		System.out.println(stationList);
	}

	public static void addEdgeAndweight(Graph<String, DefaultEdge> g, String start, String end, double weight) {
		DefaultEdge e = g.addEdge(start, end);
		g.setEdgeWeight(e, weight);
	}

	@Test
	public void shortestPathFor3StationsWithDoors() {
		Graph<String, DefaultEdge> g = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
		g.addVertex("SengKang:Start");
		g.addVertex("SengKang:1");
		g.addVertex("SengKang:2");
		g.addVertex("SengKang:3");
		g.addVertex("SengKang:End");
		g.addVertex("Buangkok:Start");
		g.addVertex("Buangkok:1");
		g.addVertex("Buangkok:2");
		g.addVertex("Buangkok:3");
		g.addVertex("Buangkok:End");
		g.addVertex("Hougang:Start");
		g.addVertex("Hougang:1");
		g.addVertex("Hougang:2");
		g.addVertex("Hougang:3");
		g.addVertex("Hougang:End");

		addEdgeAndweight(g, "SengKang:Start", "SengKang:1", 0);
		addEdgeAndweight(g,"SengKang:Start", "SengKang:2",0 );
		addEdgeAndweight(g,"SengKang:Start", "SengKang:3", 0);
		addEdgeAndweight(g,"SengKang:2", "SengKang:1", 2);
		addEdgeAndweight(g,"SengKang:2", "SengKang:3", 2);
		addEdgeAndweight(g,"SengKang:2", "SengKang:End", 0);
		addEdgeAndweight(g,"Buangkok:Start", "Buangkok:1", 0);
		addEdgeAndweight(g,"Buangkok:Start", "Buangkok:2", 0);
		addEdgeAndweight(g,"Buangkok:Start", "Buangkok:3", 0);
		addEdgeAndweight(g,"Buangkok:1", "Buangkok:2", 2);
		addEdgeAndweight(g,"Buangkok:3", "Buangkok:2", 2);
		addEdgeAndweight(g,"Buangkok:3", "Buangkok:End", 0);
		addEdgeAndweight(g,"Hougang:Start", "Hougang:1", 0);
		addEdgeAndweight(g,"Hougang:Start", "Hougang:2", 0);
		addEdgeAndweight(g,"Hougang:Start", "Hougang:3", 0);
		addEdgeAndweight(g,"Hougang:1", "Hougang:2", 2);
		addEdgeAndweight(g,"Hougang:3", "Hougang:2", 2);
		addEdgeAndweight(g,"Hougang:1", "Hougang:End", 0);

		// Add the links between each station
		addEdgeAndweight(g,"SengKang:1", "Buangkok:1", 1);
		addEdgeAndweight(g,"SengKang:2", "Buangkok:2", 1);
		addEdgeAndweight(g,"SengKang:3", "Buangkok:3", 1);
		addEdgeAndweight(g,"Hougang:1", "Buangkok:1", 1);
		addEdgeAndweight(g,"Hougang:2", "Buangkok:2", 1);
		addEdgeAndweight(g,"Hougang:3", "Buangkok:3", 1);

		BFSShortestPath<String, DefaultEdge> bfs = new BFSShortestPath<>(g);
		GraphPath<String, DefaultEdge> path = bfs.getPath("SengKang:Start", "Buangkok:End");
		List<String> stationList = path.getVertexList();
		System.out.println(stationList);
	}

	@Test
	public void getReverseTrainNumber() {

		int[] mrt_odd = 		new int[]{1, 2, 3, 4, 5};
		int[] mrt_odd_reverse = new int[]{5, 4, 3, 2, 1};
		int[] mrt_even =         new int[]{1, 2, 3, 4, 5, 6};
		int[] mrt_even_reverse = new int[]{6, 5, 4, 3, 2, 1};
	}
}
