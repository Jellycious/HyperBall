package utwente.jjw.meijer;

import java.io.File;
import java.io.IOException;

import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import utwente.jjw.meijer.hyperball.BfsTraversal;
import utwente.jjw.meijer.hyperball.DistanceDistribution;
import utwente.jjw.meijer.hyperball.Graphs;
import utwente.jjw.meijer.hyperball.HyperBall;

/**
 * Application that can analyze different graph's distance distributions.
 */
public class App 
{

    public static final String RESULTS_FOLDER = "results" + File.separator;
    public static final String GRAPHS_FOLDER = "graphs" + File.separator;
    
    public static final int NUMBER_OF_BITS_HYPERBALL = 5; // Number of bits to use for register indexing in the hyperball algorithm.

    public static void analyzeGraphWithHyperball(BVGraph graph)
    {
        String baseName = Graphs.getBasename(graph);
        String filePath = baseName + File.separator + baseName;
        analyzeGraphWithHyperball(graph, filePath);
    }
    /**
     * Analyzes a graph using the HyperBall algorithm. An appendix will be added to the filepath to indicate the algorithm used to obtain the results.
     * @param graph The graph to analyze 
     * @param filePath The filepath to store the results. For example: 'graphfolder/graphname'
     */
    public static void analyzeGraphWithHyperball(ImmutableGraph graph, String filePath)
    {
        final String FILE_APPENDIX = "-hyperball";

        HyperBall ball = new HyperBall(graph, NUMBER_OF_BITS_HYPERBALL);

        System.out.printf("Analyzing graph: %s with HyperBall\n", filePath);
        long start = System.currentTimeMillis();
        DistanceDistribution dist = ball.getDistanceDistribution();
        long end = System.currentTimeMillis();
        System.out.printf("Analysis Done\nTime taken: %dms\n", end - start);
        System.out.println("Saving results to disk\n");

        filePath = RESULTS_FOLDER + filePath + FILE_APPENDIX;
        saveDistanceDistributionAnalysis(dist, filePath);
    }

    /**
     * Analysses a BVGraph using BFS-Traversal. The names are automatically obtained from the graph.
     * @param graph Graph to analyze.
     */
    public static void analyzeGraphWithBFS(BVGraph graph)
    {
        String basename = Graphs.getBasename(graph);
        String filePath = basename + File.separator + basename;
        analyzeGraphWithBFS(graph, filePath);
    }


    /**
     * Analyzes a graph's distance distribution using BFS-Traversal. 
     * @param graph The graph to analyze
     * @param filePath The filepath to store the analysis results. For example: 'graphfolder/graphname'
     */
    public static void analyzeGraphWithBFS(ImmutableGraph graph, String filePath)
    {
        final String FILE_APPENDIX = "-bfs";

        BfsTraversal bfs = new BfsTraversal(graph);

        System.out.printf("Analyzing graph: %s with Breadth First Traversal\n", filePath);
        long start = System.currentTimeMillis();
        DistanceDistribution dist = bfs.getDistanceDistribution();
        long end = System.currentTimeMillis();

        System.out.printf("Analysis Done\nTime taken: %dms\n", end - start);
        System.out.println("Saving results to disk\n");

        filePath = RESULTS_FOLDER + filePath + FILE_APPENDIX;
        saveDistanceDistributionAnalysis(dist, filePath);
    }




    /**
     * Saves distance distribution to disk. Both the image and .dd file. 
     * @param dist  DistanceDistribution to save.
     * @param graph Graph associated with filename. Neccesary for structuring filepaths.
     */
    private static void saveDistanceDistributionAnalysis(DistanceDistribution dist, String saveFilePath)
    {
        String filepath = saveFilePath;
        try {

            File ddFile = new File(filepath + ".dd");           // distance distribution file         
            File imgFile = new File(filepath + ".png");         // distance distribution image
            //  create directory if it does not exist.
            if (!ddFile.getParentFile().exists()) ddFile.getParentFile().mkdirs();


            DistanceDistribution.saveToDisk(ddFile, dist);                        //  save distance distribution to disk.
            DistanceDistribution.saveDistanceDistributionImage(imgFile, dist);

        } catch (IOException e) {

            System.err.println("An error has occurred while saving the distance distribution");
            e.printStackTrace();

        }
    }


    public static void main( String[] args )
    {
        analyzeGraphWithBFS(Graphs.getWordAssociation2011Graph());
    }
}
