package graph;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class KosarajuTest {
    @Test
    public void testSimpleSCC() {
        List<String> ids = List.of("A","B","C","D");
        int[] dur = {3,2,4,1};
        List<int[]> edges = List.of(
                new int[]{0,1}, new int[]{1,2}, new int[]{2,0},
                new int[]{1,3}
        );
        Graph g = new Graph(ids, dur, edges);
        Metrics m = new Metrics();
        Kosaraju algo = new Kosaraju(g, m);
        int[] comp = algo.run();
        assertEquals(2, algo.getCompCount());
        int[] sizes = algo.sizes();
        Arrays.sort(sizes);
        assertArrayEquals(new int[]{1,3}, sizes);
        assertEquals(comp[0], comp[1]);
        assertEquals(comp[1], comp[2]);
        assertNotEquals(comp[2], comp[3]);
    }
}
