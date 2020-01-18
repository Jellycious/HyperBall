package utwente.jjw.meijer.hyperball;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;

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



    public static void main(String[] args) {
        ImmutableGraph graph = Graphs.getWordAssociation2011Graph();
        BfsTraversal bfs = new BfsTraversal(graph);
        // Time getReachableNodes function.
        Integer[] reachableNodes = bfs.getReachableNodes(1);

        System.out.println(reachableNodes.length);
    }
}