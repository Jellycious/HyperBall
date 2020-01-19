package utwente.jjw.meijer.hyperball;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import org.jfree.chart.JFreeChart;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.NodeIterator;

/**
 * Class for performing bfs traversal on graphs to get the distance distribution.
 * Its complexity is (n^2). Too inneficient to do graphs much larger than 10.000
 */
public class BfsTraversal
{

    private final ImmutableGraph graph;

    public BfsTraversal(ImmutableGraph graph)
    {
        if (!graph.randomAccess()){
            throw new InvalidParameterException("Graph must have random-access enabled");
        }
        this.graph = graph;
    }

    public Integer[] getReachableNodes(int v)
    {
        boolean[] discovered = new boolean[graph.numNodes()]; // Initialize empty array of labels.
        for (int i = 0; i < discovered.length; i++){
            discovered[i] = false;
        }

        // List for storing reachable nodes
        ArrayList<Integer> reachableNodes = new ArrayList<>();
        reachableNodes.add(v); // v also belongs to reachable nodes.

        Queue<Integer> queue = new LinkedList<Integer>();
        discovered[v] = true;
        queue.add(v);

        while (!queue.isEmpty()){
            int node = queue.poll();
            LazyIntIterator lazyiter = graph.successors(node);

            int neighbor;
            while ((neighbor = lazyiter.nextInt()) != -1) {
                if (!discovered[neighbor]) {
                    discovered[neighbor] = true;
                    reachableNodes.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        Integer[] result = new Integer[reachableNodes.size()];
        reachableNodes.toArray(result);
        return result;
    }


    public DistanceDistribution getDistanceDistribution(){
        // Do a BFS traversal and get the exact distance distribution of the graph. 
        NodeIterator nodeIter = graph.nodeIterator();
        DistanceDistribution distribution = new DistanceDistribution();

        while (nodeIter.hasNext()){
            int node = nodeIter.nextInt();
            updateDistanceDistribution(distribution, node);
        }
        return distribution;
    }

    /**
     * Updates distancedistribution for a node in the graph. With BFS traversal.
     * @param distribution  The distribution to update.
     * @param node The node to update.
     */

    private void updateDistanceDistribution(DistanceDistribution distribution, int node){

        boolean[] visited = new boolean[graph.numNodes()]; // keeps track of the nodes we have visited. 
        for (int i = 0; i < visited.length; i++){
            visited[i] = false;
        }
        visited[node] = true;

        Queue<BFSNode> queue = new LinkedList<>(); //Add first node to queue.
        BFSNode startNode = new BFSNode(node, 0);
        queue.add(startNode);

        // BFS Loop until no more nodes can be found.
        while(!queue.isEmpty()){ 
            BFSNode bfsNode = queue.poll();
    
            LazyIntIterator iter = graph.successors(bfsNode.node);
            int n; 
            while((n = iter.nextInt()) != -1){

                if(!visited[n]){
                    visited[n] = true;
                    int distance = bfsNode.distance + 1;
                    // Add new found node to queue.
                    queue.add(new BFSNode(n, distance)); // Distance to current node + 1
                    
                    // update distribution
                    distribution.incrementNumberOfPairs(distance);
                }
            }
        }
    }

    // HELPER CLASS 
    private class BFSNode {
        public int node;
        public int distance;

        private BFSNode(int node, int distance){
            this.node = node;
            this.distance = distance;
        }
    }

    public static void main(String[] args) {
        ImmutableGraph graph = Graphs.getWordAssociation2011Graph();
        BfsTraversal bfs = new BfsTraversal(graph);
        // Time getReachableNodes function.
        long start = System.currentTimeMillis();
        DistanceDistribution distribution = bfs.getDistanceDistribution();
        long end = System.currentTimeMillis();

        System.out.printf("BFS took: %dms\n", end-start);
        distribution.printDistribution();

        try {
            JFreeChart chart = distribution.getChart();
            BufferedImage img = chart.createBufferedImage(1000, 1000);
            // get graph name.
            String basename = graph.basename().toString();
            String[] arr = basename.split("/", 0);
            String graphName = arr[arr.length - 1];
            
            File file = new File("results/"+graphName+"-bfs.png");
            ImageIO.write(img, "png", file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}