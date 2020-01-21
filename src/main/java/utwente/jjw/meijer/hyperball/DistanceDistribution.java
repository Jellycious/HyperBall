package utwente.jjw.meijer.hyperball;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

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
public class DistanceDistribution implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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

    /**
     * Increases the number of pairs by a value increase.
     * @param distance the distance value to update.
     * @param increase the amount to increase the number of pairs value by.
     * @return The newly increased number of pairs.
     */
    public long increaseNumberOfPairs(int distance, long increase){
        Long numberOfPairs = distributionMap.get(distance);
        if (numberOfPairs != null){
            numberOfPairs = numberOfPairs + increase;
            
            if (numberOfPairs < 0) {
                System.out.printf("NEGATIVE: increase=%d and numberOfPairs=%d\n", increase, numberOfPairs);
            }

            distributionMap.put(distance, numberOfPairs);
            return numberOfPairs;
        }else {
            numberOfPairs = (long) increase;
            distributionMap.put(distance, numberOfPairs);
            return numberOfPairs;
        }
    }

    /**
     * Returns the number of pairs associated with a distance.
     * @param distance distance to get
     * @return The number of pairs associated with :param: distance;
     */
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

    /**
     * Prints the distribution to standard System outputstream.
     */
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

    /**
     * Saves the distance distribution to the disk
     * @param file  File to save to
     * @throws IOException
     */
    public static void saveToDisk(File file, DistanceDistribution dist) throws IOException 
    {
        FileOutputStream outputStream = new FileOutputStream(file);
        ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
        objectStream.writeObject(dist);
        objectStream.close();
        outputStream.close();
    } 
    
    /**
     * Deserializes a file containing and DistanceDistribution object
     * @param file File that contains DistanceDistributon object.
     * @return  DistanceDistribution contained within the file.
     * @throws IOException
     */
    public static DistanceDistribution loadFromDisk(File file) throws IOException
    {
        FileInputStream inputStream = new FileInputStream(file);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);

        try {
            DistanceDistribution dist = (DistanceDistribution) objectStream.readObject();
            return dist;

        } catch (ClassNotFoundException e){
            e.printStackTrace();
            System.err.println("File must contain DistanceDistribution Object");
            return null;

        } finally {
            
            objectStream.close();
            inputStream.close();
        }

    }
    

    /**
     * Returns a plot image of the distance distribution
     * @param filename  filename to save to
     * @param dist      distribution to save
     * @throws IOException
     */
    public static void saveDistanceDistributionImage(File file, DistanceDistribution dist) throws IOException {
        JFreeChart chart = dist.getChart();
        BufferedImage img = chart.createBufferedImage(1000, 1000);
        ImageIO.write(img, "png", file);
    }   


    public static void main(String[] args){
        File file = new File("results/completegraph-1000/completegraph-1000-bfs.dd");
        try {
            DistanceDistribution dist = DistanceDistribution.loadFromDisk(file);
            dist.printDistribution();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}   