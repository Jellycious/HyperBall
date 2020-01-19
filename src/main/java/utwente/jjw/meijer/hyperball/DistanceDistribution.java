package utwente.jjw.meijer.hyperball;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * ProbabilityDistribution Class
 * Provides utility for probability distributions.
 * Allows for things such as displaying plots and such.
 */
public class DistanceDistribution {

    // Hashmap that keeps track of the number of pairs associated with a distance.
    private final HashMap<Integer, Long> distributionMap; // key = distance, value = number of pairs 

    
    public DistanceDistribution()
    {
        this.distributionMap = new HashMap<>();
    }

    public void setNumberOfPairs(int distance, Number numberOfPairs)
    {
        distributionMap.put(distance, numberOfPairs.longValue());
    }
    

    public long getNumberOfPairs(int distance){
        return distributionMap.get(distance);
    }

    /**
     * Increments the number of pairs
     * @param distance Associated distance.
     * @return The newly incremented number of pairs.
     */
    public long incrementNumberOfPairs(int distance){
        Long numberOfPairs = distributionMap.get(distance);
        if (numberOfPairs != null){
            numberOfPairs = numberOfPairs + 1;
            distributionMap.put(distance, numberOfPairs);
            return numberOfPairs;
        }else {
            numberOfPairs = 1L;
            distributionMap.put(distance, numberOfPairs);
            return numberOfPairs;
        }
    }

    public long getValue(int distance)
    {
        if (distributionMap.containsKey(distance)){
            return distributionMap.get(distance);
        }else {
            return 0;
        }
    }
    
    /**
     * Returns total number of pairs that can reach each other.
     * @return NumberOfPairs that are able to reach each other.
     */
    public long getTotal(){
        long total = 0;
        Iterator<Long> iter = distributionMap.values().iterator();

        while (iter.hasNext()){
            long numberOfPairs = (long) iter.next();
            total = total + numberOfPairs;
        }

        return total;
    }

    /**
     * Retuns a chart that shows the distance distribution as percentages.
     * @return The Chart
     */
    public JFreeChart getChart(){
    
        BigDecimal bigsum = new BigDecimal(getTotal());
        

        // add data to dataset.
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries serie = new XYSeries("N");
        Iterator<Integer> keysIter = distributionMap.keySet().iterator();

        while (keysIter.hasNext()){

            Integer key = keysIter.next();

            BigDecimal numberOfPairs = new BigDecimal(distributionMap.get(key));
            // normalize the number of pairs.
            numberOfPairs = numberOfPairs.divide(bigsum, 5, RoundingMode.HALF_UP);

            serie.add((double) key, numberOfPairs.doubleValue());

        }

        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
            "Distance Distribution", 
            "Distance", 
            "Number of Pairs", 
            dataset, 
            PlotOrientation.VERTICAL, 
            false, true, false);

        return chart;
    }

    public void printDistribution(){
        Iterator<Integer> keys = distributionMap.keySet().iterator();
        System.out.println("Distance Distribution");
        System.out.println("Distance:   Number of Pairs:");
        while(keys.hasNext()){
            int key = keys.next();
            long val = distributionMap.get(key);
            System.out.printf("%5d %21d\n", key, val);
        }
    }


    public static void main(String[] args){
        DistanceDistribution dist = new DistanceDistribution();
        dist.setNumberOfPairs(1, 10);
        dist.setNumberOfPairs(1, 20);
        dist.setNumberOfPairs(2, 20);
        dist.setNumberOfPairs(3, 40);
        dist.setNumberOfPairs(4, 100);
        dist.setNumberOfPairs(5, 60);
        dist.setNumberOfPairs(6, 20);
        dist.setNumberOfPairs(7, 10);
        dist.getChart();
        dist.printDistribution(); 
    }
}   