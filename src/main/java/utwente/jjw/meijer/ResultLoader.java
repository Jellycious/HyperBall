package utwente.jjw.meijer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import it.unimi.dsi.webgraph.BVGraph;


import utwente.jjw.meijer.hyperball.DistanceDistribution;
import utwente.jjw.meijer.hyperball.Graphs;

public class ResultLoader {

    private final String GRAPH_RESULT_DIRECTORY;

    public static final String RESULTS_DIRECTORY = GraphAnalyzer.RESUTLS_DIRECTORY;


    /**
     * Loads results done from analysis.
     * @param graph
     */
    public ResultLoader(BVGraph graph)
    {
        String baseName = Graphs.getBasename(graph);
        this.GRAPH_RESULT_DIRECTORY = RESULTS_DIRECTORY + baseName + File.separator;
    }

    /**
     * Takes a graph and the filePath where the results of the analysis are stored.
     * 
     * @param graph
     * @param filePath
     */
    public ResultLoader(String graphName)
    {  
        this.GRAPH_RESULT_DIRECTORY = RESULTS_DIRECTORY + graphName + File.separator;
    }

    public HashMap<String, DistanceDistribution> loadDistanceDistributions() throws IOException
    {
        HashMap<String, DistanceDistribution> distMap = new HashMap<>();

        File resultDirectory = new File(this.GRAPH_RESULT_DIRECTORY); 
        if(!resultDirectory.isDirectory()){
            System.err.println("Given graph has no results directory");
            return distMap;
        }

        File[] files = resultDirectory.listFiles();
        for (int i = 0; i < files.length; i++){
            File file = files[i];

            String name = file.getName();
            String[] splitted = name.split("\\.", 0);

            if (splitted.length != 2) continue; 

            if (splitted[1].equals("dd")){              // Found distance distribution
                String algorithmName = splitted[0];
                DistanceDistribution dist = DistanceDistribution.loadFromDisk(file);
                distMap.put(algorithmName, dist);
            }
        }

        return distMap;
    }



    public static void main(String[] args)
    {
        ResultLoader loader = new ResultLoader("completegraph-1000");

        HashMap<String, DistanceDistribution> distMap;
        try {
            distMap = loader.loadDistanceDistributions();
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        Iterator<String> algorithms = distMap.keySet().iterator();
        while (algorithms.hasNext()){
            String algoName = algorithms.next();
            System.out.printf("Distribution Results for algorithm %s\n", algoName);
            distMap.get(algoName).printDistribution();
            System.out.println("\n\n");
        }
    }


}