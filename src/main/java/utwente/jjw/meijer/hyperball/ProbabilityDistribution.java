package utwente.jjw.meijer.hyperball;

import java.util.HashMap;

/**
 * ProbabilityDistribution Class
 * Provides utility for probability distributions.
 * Allows for things such as displaying plots and such.
 */
public class ProbabilityDistribution {

    // Hashmap that keeps track of the 
    private final HashMap<Integer, Integer> distributionMap; // key = distance, value = number of pairs 

    
    public ProbabilityDistribution()
    {
        this.distributionMap = new HashMap<>();
    }

    public void setDistanceValue(int distance, int numberOfPairs)
    {
        distributionMap.put(distance, numberOfPairs);
    }

    public int getValue(int distance)
    {
        if (distributionMap.containsKey(distance)){
            return distributionMap.get(distance);
        }else {
            return 0;
        }
    }
}