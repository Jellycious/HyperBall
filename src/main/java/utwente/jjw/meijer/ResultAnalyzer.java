package utwente.jjw.meijer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import it.unimi.dsi.fastutil.Hash;
import utwente.jjw.meijer.hyperball.DistanceDistribution;
import utwente.jjw.meijer.hyperball.Graphs;

public class ResultAnalyzer {

    final ResultLoader loader;

    public ResultAnalyzer(ResultLoader loader){
        this.loader = loader;
    }

    /**
     * Prints the result of both the hyperball analysis and the bfs analysis.
     * Compares hyperball against the exact values of bfs and shows error percentage.
     */
    public void analyzeError()
    {  
        HashMap<String, DistanceDistribution> distMap;

        try {
            distMap = loader.loadDistanceDistributions();
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        if (!distMap.containsKey("bfs") || !distMap.containsKey("hyperball")){
            System.err.println("Map does not contain both bfs and hyperball results");
            return;
        }

        // compare BFS against HyperBall
        DistanceDistribution bfsResult = distMap.get("bfs");
        DistanceDistribution hbResult = distMap.get("hyperball");

        Iterator<Integer> hbIter = hbResult.iterator();

        System.out.printf("%-20s%-20s%-20s%-10s\n\n","Distance:","HyperBall:","BFS:","ERROR:");

        
        long totalDiff = 0L; // total difference. 
        while (hbIter.hasNext()){
            int distance = hbIter.next();
            long hbNum = hbResult.getValue(distance);
            long bfsNum = bfsResult.getValue(distance);

            long diff = hbNum - bfsNum;
            totalDiff += Math.abs(diff); // absolute difference to prevent differences from cancelling each other out. 
            double relativeErr = (double) diff / bfsNum; 
            relativeErr = relativeErr * 100; // percentage
            
            System.out.printf("%-20d%-20d%-20d%4f%%\n", distance, hbNum, bfsNum, relativeErr);
        }

        double totalRelativeErr = (double) totalDiff / bfsResult.getTotal();

        System.out.println("\nTotal relative error: "+totalRelativeErr+"%");
        System.out.printf("Bfs observed %d pairs\nHyperBall observed %d pairs\n",bfsResult.getTotal(), hbResult.getTotal());
    }

     /**
     * Compares an exact algorithm against an approximate algorithm. And calculates error percentages
     */
    public void analyzeError(String exactAlgoName, String approxAlgoName)
    {  
        HashMap<String, DistanceDistribution> distMap;

        try {
            distMap = loader.loadDistanceDistributions();
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        if (!distMap.containsKey(exactAlgoName) || !distMap.containsKey(approxAlgoName)){
            System.err.printf("Map does not contain both %s and %s results\n", exactAlgoName, approxAlgoName);
            return;
        }

        // compare BFS against HyperBall
        DistanceDistribution exactDist = distMap.get(exactAlgoName);
        DistanceDistribution approxDist = distMap.get(approxAlgoName);

        Iterator<Integer> approxIter = approxDist.iterator();

        System.out.printf("%-20s%-20s%-20s%-10s\n\n","Distance:",approxAlgoName, exactAlgoName,"ERROR:");

        
        long totalDiff = 0L; // total difference. 
        while (approxIter.hasNext()){
            int distance = approxIter.next();
            long hbNum = approxDist.getValue(distance);
            long bfsNum = exactDist.getValue(distance);

            long diff = hbNum - bfsNum;
            totalDiff += Math.abs(diff); // absolute difference to prevent differences from cancelling each other out. 
            double relativeErr = (double) diff / bfsNum; 
            relativeErr = relativeErr * 100; // percentage
            
            System.out.printf("%-20d%-20d%-20d%4f%%\n", distance, hbNum, bfsNum, relativeErr);
        }

        double totalRelativeErr = (double) totalDiff / exactDist.getTotal();

        System.out.println("\nTotal relative error: "+totalRelativeErr+"%");
        System.out.printf("%s observed %d pairs\n%s observed %d pairs\n", exactAlgoName, exactDist.getTotal(), approxAlgoName, approxDist.getTotal());
    }

    /**
     * Prints the results of the hyperball analysis
     */
    public void printHyperBallResults(){
        HashMap<String, DistanceDistribution> distMap;
        try {
            distMap = loader.loadDistanceDistributions();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (!distMap.containsKey("hyperball")){
            System.err.println("No hyperball results available");
            return;
        }

        DistanceDistribution dist = distMap.get("hyperball");
        printProbabilityMass(dist);
        System.out.println();
        printStats(dist);
    }


    private void printProbabilityMass(DistanceDistribution dist){
        HashMap<Integer, Double> probMass = dist.getProbabilityMass();
        System.out.printf("%-20s%-20s\n", "Distance:", "Probability:");
        
        Iterator<Integer> keyIter = probMass.keySet().iterator();
        while (keyIter.hasNext()){
            int distance = keyIter.next();
            System.out.printf("%-20d%-20f\n", distance, probMass.get(distance));
        }
    }

    private void printStats(DistanceDistribution dist){
        System.out.printf("%-10s%10f\n","Mean:", dist.getMean());
        System.out.printf("%-10s%10f\n","Variance:", dist.getVariance());
        System.out.printf("%-10s%10f\n","Spid:", dist.getSpid());
    }





    public static void main(String[] args)
    {
        ResultLoader loader = new ResultLoader("wordassociation-2011");
        ResultAnalyzer analyzer = new ResultAnalyzer(loader);
        analyzer.analyzeError("bfs", "hyperball-128");
    }
}