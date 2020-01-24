package utwente.jjw.meijer.hyperball;

import java.io.IOException;

import it.unimi.dsi.webgraph.ArrayListMutableGraph;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.examples.ErdosRenyiGraph;

public class Graphs {

    /**
     * Returns Erdos-Renyi graph. A well known model for creating graphs.
     * 
     * @param n number of nodes
     * @param p probability of edge forming
     * @return Erdos-Renyi graph as ImmutableGraph
     */
    public static ImmutableGraph getErdosRenyiGraph(int n, double p) {
        ErdosRenyiGraph graph = new ErdosRenyiGraph(n, p);
        ArrayListMutableGraph mutGraph = new ArrayListMutableGraph(graph);
        ImmutableGraph g = mutGraph.immutableView();
        return g;
    }

    /**
     * Returns a complete graph. Every node is connected to all other nodes.
     * 
     * @param n Number of nodes
     * @return Complete Graph as ImmutableGraph
     */
    public static ImmutableGraph getCompleteGraph(int n) {
        ArrayListMutableGraph graph = new ArrayListMutableGraph(getErdosRenyiGraph(n, 1.0));
        ImmutableGraph g = graph.immutableView();
        return g;
    }

    /**
     * Loads a graph from a .graph file. Required files are basename.graph,
     * basename.properties and basename.offsets. The .offsets file can be generated
     * with generateOffsets() function. Typical usage:
     * loadWebGraphFromFile("/graphs/wordassociation-2011/wordassocation-2011") Note
     * that the .graph is omitted.
     * 
     * @param filePath
     * @return Graph specified in the file.
     */
    public static ImmutableGraph loadWebGraphFromFile(String filePath) throws IOException {
        ImmutableGraph graph = ImmutableGraph.load(filePath);
        return graph;
    }

    /**
     * To use a graph you first need to generate the .offsets file. This can be done
     * with the WebGraph library. This is my hacky way of generating the properties
     * file using our projects dependencies as a basis.
     * 
     * @param filepath Graph to generate .offsets file for.
     */
    public static void generateOffsets(String filepath) throws Exception {
        String[] args = new String[3];
        args[0] = "-o";
        args[1] = "-O";
        args[2] = filepath;
        // BVGraph main function to generate the file.
        BVGraph.main(args);
    }

    public static BVGraph loadBVGraphFromFile(String filepath) throws IOException {
        BVGraph graph = BVGraph.load(filepath);
        return graph;
    }

    public static BVGraph getWordAssociation2011Graph() {
        BVGraph graph;
        try {
            graph = loadBVGraphFromFile("graphs/wordassociation-2011/wordassociation-2011");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    public static BVGraph getDPLB2010Graph() {
        BVGraph graph;
        try {
            graph = loadBVGraphFromFile("graphs/dblp-2010/dblp-2010");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    public static BVGraph getHollywood2009Graph() {
        BVGraph graph;
        try {
            graph = loadBVGraphFromFile("graphs/hollywood-2009/hollywood-2009");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    public static BVGraph getEu2015Host() {
        BVGraph graph;
        try {
            graph = loadBVGraphFromFile("graphs/eu-2015-host/eu-2015-host");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    public static BVGraph getUk2014Host() {
        BVGraph graph;
        try {
            graph = loadBVGraphFromFile("graphs/uk-2014-host/uk-2014-host");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    public static String getBasename(BVGraph graph) {
        String basename = graph.basename().toString();
        String[] arr = basename.split("/", 0);
        String graphName = arr[arr.length - 1];
        return graphName;
    }

    /**
     * This is how you generate a .offsets file for your downloaded graph.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // You can generate the graphs offsets here

        try {
            generateOffsets("graphs/uk-2014-host/uk-2014-host");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}