package utwente.jjw.meijer;

import it.unimi.dsi.webgraph.ImmutableGraph;

/**
 * Application that can analyze different graph's distance distributions.
 */
public class App 
{

    public static final String RESULTS_FOLDER = "results/";
    public static final String GRAPHS_FOLDER = "graphs/";

    /**
     * Analyses a graph using hyperball and optionally bfs. 
     * It will save the results for later usage
     * @param graph
     */
    public static void analyzeGraphHyperBall(ImmutableGraph graph){

    }


    public static void main( String[] args )
    {
        boolean a = false;
        boolean b = true;
        System.out.println(a);
        a = a | b;
        System.out.println(a);
    }
}
