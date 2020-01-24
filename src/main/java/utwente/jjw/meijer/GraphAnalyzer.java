package utwente.jjw.meijer;

import java.io.File;
import java.io.IOException;

import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import utwente.jjw.meijer.hyperball.BfsTraversal;
import utwente.jjw.meijer.hyperball.DistanceDistribution;
import utwente.jjw.meijer.hyperball.Graphs;
import utwente.jjw.meijer.hyperball.HyperBall;
import utwente.jjw.meijer.utilities.Utilities;

/**
 * Application that can analyze different graph's distance distributions.
 */
public class GraphAnalyzer 
{

    public static final String RESUTLS_DIRECTORY = "results" + File.separator;
    public static final String GRAPHS_DIRECTORY = "graphs" + File.separator;
    
    
    public static final String BFS_KEY = "bfs";
    public static final String HYPERBALL_KEY = "hyperball";



    public static final int NUMBER_OF_BITS_HYPERBALL = 5; // Number of bits to use for register indexing in the hyperball algorithm.


    /**
     * Analyze a BVGraph with the HyperBall algorithm. 
     * Will store the results in the appropriate folder. 
     * @param graph Graph to analyze.
     */
    public static void analyzeGraphWithHyperball(BVGraph graph)
    {
        String baseName = Graphs.getBasename(graph);
        String resultsFolder = baseName + File.separator;
        analyzeGraphWithHyperball(graph, resultsFolder);
    }


    /**
     * Analyzes a graph using the HyperBall algorithm. An appendix will be added to the filepath to indicate the algorithm used to obtain the results.
     * @param graph The graph to analyze 
     * @param filePath The filepath to store the results. For example: 'graphfolder/graphname'
     */
    public static void analyzeGraphWithHyperball(ImmutableGraph graph, String resultsFolder)
    {
        final String FILE_NAME = HYPERBALL_KEY;

        HyperBall ball = new HyperBall(graph, NUMBER_OF_BITS_HYPERBALL);

        System.out.printf("Analyzing graph: %s containing %d nodes with HyperBall\n", resultsFolder, graph.numNodes());
        long start = System.currentTimeMillis();
        DistanceDistribution dist = ball.getDistanceDistribution();
        long end = System.currentTimeMillis();
        System.out.printf("Analysis Done\nTime taken: %dms\n", end - start);
        System.out.println("Saving results to disk\n");

        String filePath = RESUTLS_DIRECTORY + resultsFolder + File.separator + FILE_NAME;
        saveDistanceDistributionAnalysis(dist, filePath);
    }

    /**
     * Runs hyperball with a custom number of registers.
     * @param graph graph to analyze.
     * @param b number of bits to use for register indexing.
     */
    public static void analyzeGraphWithHyperball(BVGraph graph, int b)
    {
        int numberReg = (int) Math.pow(2, b);   // number of registers

        String baseName = Graphs.getBasename(graph);
        String resultsFolder = baseName + File.separator;

        HyperBall ball = new HyperBall(graph, b);

        System.out.printf("Analyzing graph: %s containing %d nodes with HyperBall using %d registers.\n", resultsFolder, graph.numNodes(), numberReg);
        long start = System.currentTimeMillis();
        DistanceDistribution dist = ball.getDistanceDistribution();
        long end = System.currentTimeMillis();
        System.out.printf("Analysis Done\nTime taken: %dms\n", end - start);
        System.out.println("Saving results to disk\n");

        final String FILE_NAME = HYPERBALL_KEY;
        String filePath = RESUTLS_DIRECTORY + resultsFolder + File.separator + FILE_NAME + "-" + numberReg;

        saveDistanceDistributionAnalysis(dist, filePath);
        

    }


    /**
     * Analysses a BVGraph using BFS-Traversal. The names are automatically obtained from the graph.
     * @param graph Graph to analyze.
     */
    public static void analyzeGraphWithBFS(BVGraph graph)
    {
        String baseName = Graphs.getBasename(graph);
        String resultsFolder = baseName + File.separator;
        analyzeGraphWithBFS(graph, resultsFolder);
    }


    /**
     * Analyzes a graph's distance distribution using BFS-Traversal. 
     * @param graph The graph to analyze
     * @param filePath The filepath to store the analysis results. For example: 'graphfolder/graphname'
     */
    public static void analyzeGraphWithBFS(ImmutableGraph graph, String resultsFolder)
    {
        final String FILE_NAME = BFS_KEY;

        BfsTraversal bfs = new BfsTraversal(graph);

        System.out.printf("Analyzing graph: %s containing %d nodes with Breadth First Traversal\n", resultsFolder, graph.numNodes());
        long start = System.currentTimeMillis();
        DistanceDistribution dist = bfs.getDistanceDistribution();
        long end = System.currentTimeMillis();

        System.out.printf("Analysis Done\nTime taken: %dms\n", end - start);
        System.out.println("Saving results to disk\n");

        String filePath = RESUTLS_DIRECTORY + resultsFolder + File.separator + FILE_NAME;
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
            File barImgFile = new File(filepath + "-bar.png");
            //  create directory if it does not exist.
            if (!ddFile.getParentFile().exists()) ddFile.getParentFile().mkdirs();


            DistanceDistribution.saveToDisk(ddFile, dist);                        //  save distance distribution to disk.
            DistanceDistribution.saveDistanceDistributionImage(imgFile, dist);
            DistanceDistribution.saveDistanceDistributionImageBar(barImgFile, dist);

        } catch (IOException e) {

            System.err.println("An error has occurred while saving the distance distribution");
            e.printStackTrace();

        }
    }


    public static void main( String[] args )
    {
        BVGraph graph = Graphs.getDPLB2010Graph();
        analyzeGraphWithBFS(graph);
        

    }
}
