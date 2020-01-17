package utwente.jjw.meijer.hyperball;

import java.io.IOException;

import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.examples.ErdosRenyiGraph;


public class Graphs{


    /**
     * Returns Erdos-Renyi graph. A well known model for creating graphs.
     * @param n number of nodes
     * @param p probability of edge forming
     * @return  Erdos-Renyi graph as ImmutableGraph
     */
    public static ImmutableGraph getErdosRenyiGraph(int n, double p)
    {
        ImmutableGraph graph = new ErdosRenyiGraph(n, p);
        return graph;
    }

    /**
     * Returns a complete graph. Every node is connected to all other nodes.
     * @param n Number of nodes
     * @return Complete Graph as ImmutableGraph
     */
    public static ImmutableGraph getCompleteGraph(int n)
    {
        ImmutableGraph graph = new ErdosRenyiGraph(n, 1.0);
        return graph;
    }

    /**
     * Loads a graph from a .graph file. 
     * Required files are basename.graph, basename.properties and basename.offsets.
     * The .offsets file can be generated with generateOffsets() function.
     * Typical usage: loadWebGraphFromFile("/graphs/wordassociation-2011/wordassocation-2011") Note that the .graph is omitted.
     * @param filePath
     * @return Graph specified in the file.
     */
    public static ImmutableGraph loadWebGraphFromFile(String filePath) throws IOException
    {
        ImmutableGraph graph = ImmutableGraph.load(filePath);
        return graph;
    }



    /**
     * To use a graph you first need to generate the .offsets file. This can be done with the WebGraph library.
     * This is my hacky way of generating the properties file using our projects dependencies as a basis. 
     * @param filepath Graph to generate .offsets file for.
     */
    public static void generateOffsets(String filepath) throws Exception
    {
        String[] args = new String[3];
        args[0] = "-o";
        args[1] = "-O";
        args[2] = filepath;
        // BVGraph main function to generate the file. 
        BVGraph.main(args);
    }

    
    public static ImmutableGraph getWordAssociation2011Graph(){
        ImmutableGraph graph;
        try {
            graph = loadWebGraphFromFile("graphs/wordassociation-2011/wordassociation-2011");
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return graph;
    }


    /**
     * QUICK DIRTY TESTING
     * @param args
     */
    public static void main(String[] args)
    {
        ImmutableGraph graph = getWordAssociation2011Graph();
        System.out.print(graph);
    }


}