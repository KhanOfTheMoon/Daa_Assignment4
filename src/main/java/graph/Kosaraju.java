package graph;

import java.util.*;

public class Kosaraju {
    private final Graph g;
    private final Metrics m;
    private int compCount = 0;
    private int[] compId;

    public Kosaraju(Graph g, Metrics m) { this.g = g; this.m = m; }

    public int[] run() {
        m.startTimer("scc_time");
        int n = g.n();
        compId = new int[n];
        Arrays.fill(compId, -1);

        boolean[] vis = new boolean[n];
        List<Integer> order = new ArrayList<>(n);
        for (int v = 0; v < n; v++) if (!vis[v]) dfs1(v, vis, order);

        List<List<Integer>> radj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) radj.add(new ArrayList<>());
        for (int u = 0; u < n; u++) for (int v : g.neighbors(u)) radj.get(v).add(u);

        Arrays.fill(vis, false);
        for (int i = order.size() - 1; i >= 0; i--) {
            int v = order.get(i);
            if (!vis[v]) { dfs2(v, compCount, vis, radj); compCount++; }
        }
        m.stopTimer("scc_time");
        m.inc("scc_components", compCount);
        return compId;
    }

    private void dfs1(int u, boolean[] vis, List<Integer> order) {
        vis[u] = true;
        m.inc("scc_dfs_visits", 1);
        for (int v : g.neighbors(u)) {
            m.inc("scc_edge_traversals", 1);
            if (!vis[v]) dfs1(v, vis, order);
        }
        order.add(u);
    }

    private void dfs2(int u, int cid, boolean[] vis, List<List<Integer>> radj) {
        vis[u] = true;
        compId[u] = cid;
        m.inc("scc_dfs_visits", 1);
        for (int v : radj.get(u)) {
            m.inc("scc_edge_traversals", 1);
            if (!vis[v]) dfs2(v, cid, vis, radj);
        }
    }

    public int getCompCount() { return compCount; }
    public int[] sizes() {
        int[] sz = new int[compCount];
        for (int v = 0; v < g.n(); v++) sz[compId[v]]++;
        return sz;
    }
}
