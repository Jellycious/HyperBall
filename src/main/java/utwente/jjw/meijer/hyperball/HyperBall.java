package utwente.jjw.meijer.hyperball;

import java.io.IOException;
import java.security.InvalidParameterException;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.NodeIterator;

import utwente.jjw.meijer.hll.HLLCounter;

/**
 * The hyperball algorithm. Used to compute the distance distribution of big graphs. Using probabilistic counters.
 */

public class HyperBall {

    private final ImmutableGraph GRAPH;
    private final int NUMBER_OF_BITS;
    
    /**
     * Creates a wrapper for the HyperBall algorithm.
     * @param graph The graph to analyse
     * @param numberOfBits The number of registers. More results in better accuracy.
     * @throws InvalidParameterException    
     */
    public HyperBall(ImmutableGraph graph, int numberOfBits) throws InvalidParameterException{
        if (!graph.randomAccess()){
            throw new InvalidParameterException("Graph must allow for random access");
        }else if (numberOfBits > 32){
            throw new InvalidParameterException("Number of Bits is too large");
        }
        this.NUMBER_OF_BITS = numberOfBits;
        this.GRAPH = graph;  
    }

    /**
     * Get the approximate distance distribution calculated by the HyperBall algorithm.
     * @return Distance Distribution.
     */
    public DistanceDistribution getDistanceDistribution() {

        DistanceDistribution dist = new DistanceDistribution();

        // Get initialized counters
        HLLCounter[] counters = initializeCounters();

        int t = 0; // distance
        boolean countersChanged = true;
        while (countersChanged){
            countersChanged = false;    // should change to true if the counters indeed have changed
            NodeIterator nodeIter = GRAPH.nodeIterator();
            HLLCounter[] newCounters = new HLLCounter[GRAPH.numNodes()];

            // iterate over all graph nodes.
            while (nodeIter.hasNext()){
                int node = nodeIter.nextInt();
                // create a counter copy.
                newCounters[node] = new HLLCounter(counters[node]);
                
                LazyIntIterator neighborIter = GRAPH.successors(node);

                // iterate over all neighbors.
                int neighbor;
                while ((neighbor = neighborIter.nextInt()) != -1){
                    boolean changed = newCounters[node].union(counters[neighbor]); 
                    countersChanged = countersChanged | changed; // Change countersChanged to true, if there has been a change.
                }

                // Update the distance distribution
                long oldSize = counters[node].getSize();
                long newSize = newCounters[node].getSize();
                dist.increaseNumberOfPairs(t+1, newSize - oldSize);

                //  Write result to disk. This would half the memory usage. TODO
            }

            counters = newCounters;     // replace the old counters with the new counters
            t = t + 1;                  // update distance

        }

        return dist;
    }

    /**
     * Initialises an array of counters. Furthermore adds the respective node to the counters as their first item.
     * @return Initialized array of HyperLogLog Counters with initial nodes added.
     */
    private HLLCounter[] initializeCounters(){

        HLLCounter[] counters = new HLLCounter[GRAPH.numNodes()];
        for (int i = 0; i < counters.length; i++){
            counters[i] = new HLLCounter(NUMBER_OF_BITS);
        }
        
        NodeIterator iter = GRAPH.nodeIterator();
        while (iter.hasNext()){
            int node = iter.nextInt();
            counters[node].add(node);
        }
        
        return counters;
    }

    /**
     * Analyze a graph and save its distance distribution as an image.
     * @param graph The graph to analyze.
     */
    public static void analyzeGraph(ImmutableGraph graph){
        HyperBall hyperBall = new HyperBall(graph, 5); // 2^5=32 registers per node
        DistanceDistribution dist = hyperBall.getDistanceDistribution();

        //Get graph name. Works only for BVGraphs!
        String basename = graph.basename().toString();
        String[] basenamearr = basename.split("/",0);
        String graphName = basenamearr[basenamearr.length - 1];

        dist.printDistribution(); // Print distribution
        try {
            //Try to save the distribution as an image.
            DistanceDistribution.createDistanceDistributionImage("results/"+graphName+"-hyperball.png", dist);
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args){
        ImmutableGraph graph = Graphs.getWordAssociation2011Graph();
        long start = System.currentTimeMillis();
        analyzeGraph(graph);
        long end = System.currentTimeMillis();
        System.out.printf("Time to analyze: %dms\n", end - start);
    }

}   