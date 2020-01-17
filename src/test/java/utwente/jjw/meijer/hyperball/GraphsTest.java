package utwente.jjw.meijer.hyperball;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.NodeIterator;

public class GraphsTest {



    @Test 
    public void testCompleteGraph(){
        int nodes = 10;
        int expectedDegree = nodes - 1;
        ImmutableGraph completeGraph = Graphs.getCompleteGraph(nodes);
        
        NodeIterator iterator = completeGraph.nodeIterator();

        int count = 0;

        // Check that all nodes have degree 'nodes'-1.
        while (iterator.hasNext()){ 
            iterator.nextInt();
            assertEquals(expectedDegree, iterator.outdegree());

            count = count + 1;
        }

        // Check that the loop has been run 'nodes' times.
        assertEquals(nodes, count);
    }
}