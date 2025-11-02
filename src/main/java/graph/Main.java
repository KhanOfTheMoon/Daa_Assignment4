package graph;

import java.util.*;

public class Main {
    private static void usage() {
        System.out.println("""
            Usage:
              java -jar smart-city-scheduler.jar --data <path.json> --task <scc|topo|dagsp|all> [--source <NODE_ID|Ck>] [--target <NODE_ID|Ck>]
            """);
    }

    public static void main(String[] args) throws Exception {
        String data = null, task = "all", source = null, targetOpt = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--data" -> data = args[++i];
                case "--source" -> source = args[++i];
                case "--target" -> targetOpt = args[++i];
                case "--task" -> task = args[++i];
            }
        }
        if (data == null) { usage(); return; }
        Graph g = GraphIO.loadJson(data);
        Metrics m = new Metrics();

        // SCC
        Kosaraju scc = new Kosaraju(g, m);
        int[] comp = scc.run();
        int compCount = scc.getCompCount();
        int[] compSizes = scc.sizes();

        // condensation (node-duration model)
        int[] compDur = new int[compCount];
        for (int v = 0; v < g.n(); v++) compDur[comp[v]] += g.duration(v);
        Graph dag = Graph.condensation(g, comp, compCount, compDur);

        if (task.equals("scc") || task.equals("all")) {
            List<List<String>> buckets = new ArrayList<>();
            for (int c = 0; c < compCount; c++) buckets.add(new ArrayList<>());
            for (int v = 0; v < g.n(); v++) buckets.get(comp[v]).add(g.idOf(v));
            for (int c = 0; c < compCount; c++) {
                System.out.println("C"+c+" size="+compSizes[c]+" "+buckets.get(c));
            }
            System.out.println("Components="+compCount);
        }

        // topo + derived order
        List<Integer> topo = null;
        if (task.equals("topo") || task.equals("all") || task.equals("dagsp")) {
            topo = Kahn.order(dag, m);
            System.out.println("Topo components = "+topo);
            List<List<String>> members = new ArrayList<>();
            for (int c = 0; c < compCount; c++) members.add(new ArrayList<>());
            for (int v = 0; v < g.n(); v++) members.get(comp[v]).add(g.idOf(v));
            List<String> derived = new ArrayList<>();
            for (int c : topo) derived.addAll(members.get(c));
            System.out.println("Derived order of original tasks = "+derived);
        }

        // DAG-SP
        if (task.equals("dagsp") || task.equals("all")) {
            int sComp;
            if (source == null) sComp = topo.get(0);
            else if (source.startsWith("C")) sComp = Integer.parseInt(source.substring(1));
            else sComp = comp[g.idxOf(source)];

            int tComp;
            if (targetOpt == null) tComp = topo.get(topo.size()-1);
            else if (targetOpt.startsWith("C")) tComp = Integer.parseInt(targetOpt.substring(1));
            else tComp = comp[g.idxOf(targetOpt)];

            DagSP.Result sp = DagSP.shortestNodeWeighted(dag, topo, sComp, m);
            DagSP.LongestResult lp = DagSP.longestNodeWeighted(dag, topo, sComp, m);

            System.out.println("Shortest distances from C"+sComp+":");
            for (int i = 0; i < dag.n(); i++) {
                int d = sp.dist[i];
                System.out.println("  C"+i+" = "+(d==Integer.MAX_VALUE ? "INF" : d));
            }
            System.out.println("One optimal shortest path to C"+tComp+" = "+sp.reconstruct(tComp));

            List<Integer> crit = lp.criticalPath();
            int len = (lp.end == -1 ? 0 : lp.best[lp.end]);
            System.out.println("Critical path (longest) = "+crit+" length="+len);
        }

        System.out.println("Metrics:");
        m.all().forEach((k,v) -> System.out.println(k+"="+v));
    }
}
