package graph;

import java.util.*;

public class Kahn {
    public static List<Integer> order(Graph g, Metrics m) {
        m.startTimer("topo_time");
        int n = g.n();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (int v : g.neighbors(u)) indeg[v]++;
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) { q.add(i); m.inc("kahn_pushes",1); }
        List<Integer> out = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.remove();
            m.inc("kahn_pops",1);
            out.add(u);
            for (int v : g.neighbors(u)) {
                if (--indeg[v] == 0) { q.add(v); m.inc("kahn_pushes",1); }
            }
        }
        m.stopTimer("topo_time");
        if (out.size() != n) throw new IllegalStateException("Graph has a cycle");
        return out;
    }
}
