package graph;

import java.util.*;

public class DagSP {
    public static class Result {
        public final int source;
        public final int[] dist;
        public final int[] parent;
        public Result(int source, int[] dist, int[] parent) {
            this.source = source; this.dist = dist; this.parent = parent;
        }
        public List<Integer> reconstruct(int target) {
            if (dist[target] == Integer.MAX_VALUE) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            for (int v = target; v != -1; v = parent[v]) path.add(v);
            Collections.reverse(path);
            return path;
        }
    }

    public static Result shortestNodeWeighted(Graph dag, List<Integer> topo, int s, Metrics m) {
        int n = dag.n();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[s] = dag.duration(s);
        for (int u : topo) {
            if (dist[u] == Integer.MAX_VALUE) continue;
            for (int v : dag.neighbors(u)) {
                m.inc("dag_relax_attempts",1);
                int cand = dist[u] + dag.duration(v);
                if (cand < dist[v]) { dist[v] = cand; parent[v] = u; m.inc("dag_relax_success",1); }
            }
        }
        return new Result(s, dist, parent);
    }

    public static class LongestResult {
        public final int source;
        public final int[] best;
        public final int[] parent;
        public final int end;
        public LongestResult(int source, int[] best, int[] parent, int end) {
            this.source = source; this.best = best; this.parent = parent; this.end = end;
        }
        public List<Integer> criticalPath() {
            if (end == -1 || best[end] == Integer.MIN_VALUE) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            for (int v = end; v != -1; v = parent[v]) path.add(v);
            Collections.reverse(path);
            return path;
        }
    }

    public static LongestResult longestNodeWeighted(Graph dag, List<Integer> topo, int s, Metrics m) {
        int n = dag.n();
        int[] best = new int[n];
        int[] parent = new int[n];
        Arrays.fill(best, Integer.MIN_VALUE);
        Arrays.fill(parent, -1);
        best[s] = dag.duration(s);
        for (int u : topo) {
            if (best[u] == Integer.MIN_VALUE) continue;
            for (int v : dag.neighbors(u)) {
                int cand = best[u] + dag.duration(v);
                if (cand > best[v]) { best[v] = cand; parent[v] = u; }
            }
        }
        int end = -1, max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) if (best[i] > max) { max = best[i]; end = i; }
        return new LongestResult(s, best, parent, end);
    }
}
