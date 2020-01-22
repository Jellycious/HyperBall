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

        System.out.printf("%-20s%-20s%-20s%-10s\n\n","Distance:","HyperBall:","BFS:","ERROR:");
        long totalDiff = 0L;
        while (hbIter.hasNext()){
            int distance = hbIter.next();
            long hbNum = hbResult.getValue(distance);
            long bfsNum = bfsResult.getValue(distance);

            long diff = hbNum - bfsNum;
            totalDiff = totalDiff + diff;
            double err = (double) diff / bfsNum; 
            err = err * 100; // percentage
            
            System.out.printf("%-20d%-20d%-20d%4f%%\n", distance, hbNum, bfsNum, err);
        }
        double totalErr = (double) totalDiff / bfsResult.getTotal() * 100;
        System.out.println("\nTotal relative error: "+totalErr+"%");
        System.out.printf("Bfs observed %d pairs\nHyperBall observed %d pairs\n",bfsResult.getTotal(), hbResult.getTotal());
    }






    public static void main(String[] args)
    {
        ResultLoader loader = new ResultLoader("wordassociation-2011");
        ResultAnalyzer analyzer = new ResultAnalyzer();

        try {
            HashMap<String, DistanceDistribution> distMap = loader.loadDistanceDistributions();
            analyzer.analyzeError(distMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}