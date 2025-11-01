package graph;

import java.util.*;

public class Graph {
    private final Map<String,Integer> idToIdx = new HashMap<>();
    private final List<String> idxToId = new ArrayList<>();
    private final List<List<Integer>> adj = new ArrayList<>();
    private final int[] duration;
    private final int n;

    public Graph(List<String> ids, int[] durations, List<int[]> edges) {
        this.n = ids.size();
        this.duration = durations;
        for (int i = 0; i < n; i++) {
            idToIdx.put(ids.get(i), i);
            idxToId.add(ids.get(i));
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
    }

    public int n() { return n; }
    public int duration(int v) { return duration[v]; }
    public List<Integer> neighbors(int v) { return adj.get(v); }
    public String idOf(int idx) { return idxToId.get(idx); }
    public Integer idxOf(String id) { return idToIdx.get(id); }

    public static Graph condensation(Graph g, int[] compId, int compCount, int[] compDur) {
        List<String> ids = new ArrayList<>(compCount);
        for (int c = 0; c < compCount; c++) ids.add("C"+c);
        Set<Long> seen = new HashSet<>();
        List<int[]> edges = new ArrayList<>();
        for (int u = 0; u < g.n(); u++) {
            int cu = compId[u];
            for (int v : g.neighbors(u)) {
                int cv = compId[v];
                if (cu != cv) {
                    long key = (((long)cu) << 32) ^ (cv & 0xffffffffL);
                    if (seen.add(key)) edges.add(new int[]{cu, cv});
                }
            }
        }
        return new Graph(ids, compDur, edges);
    }
}
