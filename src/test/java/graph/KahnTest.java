package graph;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class KahnTest {
    @Test
    public void testTopoOnDAG() {
        List<String> ids = List.of("A","B","C","D");
        int[] dur = {1,1,1,1};
        List<int[]> edges = List.of(
                new int[]{0,1}, new int[]{0,2}, new int[]{1,3}, new int[]{2,3}
        );
        Graph g = new Graph(ids, dur, edges);
        Metrics m = new Metrics();
        List<Integer> order = Kahn.order(g, m);
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertEquals(3, order.get(3));
    }

    @Test
    public void testCycleThrows() {
        List<String> ids = List.of("A","B");
        int[] dur = {1,1};
        List<int[]> edges = List.of(new int[]{0,1}, new int[]{1,0});
        Graph g = new Graph(ids, dur, edges);
        Metrics m = new Metrics();
        assertThrows(IllegalStateException.class, () -> Kahn.order(g, m));
    }
}
