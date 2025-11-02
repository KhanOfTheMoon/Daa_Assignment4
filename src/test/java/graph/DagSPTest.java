package graph;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DagSPTest {
    @Test
    public void testSPandLP() {
        List<String> ids = List.of("S","X","Y","T");
        int[] dur = {2,3,7,5};
        List<int[]> edges = List.of(
                new int[]{0,1}, new int[]{1,3},
                new int[]{0,2}, new int[]{2,3}
        );
        Graph dag = new Graph(ids, dur, edges);
        Metrics m = new Metrics();
        List<Integer> topo = Kahn.order(dag, m);

        DagSP.Result sp = DagSP.shortestNodeWeighted(dag, topo, 0, m);
        DagSP.LongestResult lp = DagSP.longestNodeWeighted(dag, topo, 0, m);

        assertEquals(10, sp.dist[3]); // S->X->T (2+3+5)
        assertEquals(14, lp.best[3]); // S->Y->T (2+7+5)
        List<Integer> crit = lp.criticalPath();
        assertEquals(0, crit.get(0));
        assertEquals(3, crit.get(crit.size()-1));
    }
}
