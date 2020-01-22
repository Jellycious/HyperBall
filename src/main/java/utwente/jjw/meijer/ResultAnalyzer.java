package utwente.jjw.meijer;

import java.util.HashMap;
import java.util.Iterator;

import utwente.jjw.meijer.hyperball.DistanceDistribution;

public class ResultAnalyzer {

    public void analyzeError(HashMap<String, DistanceDistribution> distMap)
    {
        if (!distMap.containsKey("bfs") || !distMap.containsKey("hyperball")){
            System.err.println("Map does not contain both bfs and hyperball results");
            return;
        }

        // compare BFS against HyperBall
        DistanceDistribution bfsResult = distMap.get("bfs");
        DistanceDistribution hbResult = distMap.get("hyperball");

        Iterator<Integer> hbIter = hbResult.iterator();

        System.out.printf("%-20s %-20s %-10s\n\n","HyperBall:","BFS:","ERROR:");
        while (hbIter.hasNext()){
            int distance = hbIter.next();
            long hbNum = hbResult.getValue(distance);
            long bfsNum = bfsResult.getValue(distance);

            long diff = hbNum - bfsNum;
            double err = (double) diff / bfsNum; 
            err = err * 100; // percentage
            
            System.out.printf("%-20d %-20d %4f%%\n", hbNum, bfsNum, err);
        }
    
        

    }






    public static void main(String[] args)
    {
        ResultLoader loader = new ResultLoader("completegraph-1000");
        ResultAnalyzer analyzer = new ResultAnalyzer();

        try {
            HashMap<String, DistanceDistribution> distMap = loader.loadDistanceDistributions();
            analyzer.analyzeError(distMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}