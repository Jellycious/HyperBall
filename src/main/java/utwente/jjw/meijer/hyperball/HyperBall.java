package utwente.jjw.meijer.hyperball;

import java.security.InvalidParameterException;

import it.unimi.dsi.webgraph.ImmutableGraph;

/**
 * The hyperball algorithm. Used to compute the distance distribution of big graphs. Using probabilistic counters.
 */

public class HyperBall {

    private final ImmutableGraph graph;
    
    public HyperBall(ImmutableGraph graph) throws InvalidParameterException{
        if (!graph.randomAccess()){
            throw new InvalidParameterException("Graph must allow for random access");
        }
        this.graph = graph;  
    }




    
}