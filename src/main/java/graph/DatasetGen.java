package graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import java.io.File;
import java.util.*;

public class DatasetGen {
    private static final Random RNG = new Random(42);

    private static ObjectNode edge(ObjectMapper om, String from, String to) {
        ObjectNode e = om.createObjectNode();
        e.put("from", from);
        e.put("to", to);
        return e;
    }

    private static ObjectNode makeGraph(int n, double density, boolean cycles, String name, String desc) {
        ObjectMapper om = new ObjectMapper();
        ObjectNode root = om.createObjectNode();

        ObjectNode meta = om.createObjectNode();
        meta.put("name", name);
        meta.put("description", desc);
        meta.put("duration_unit", "minutes");
        root.set("metadata", meta);

        ArrayNode nodes = om.createArrayNode();
        for (int i = 0; i < n; i++) {
            ObjectNode nd = om.createObjectNode();
            nd.put("id", "T"+i);
            nd.put("duration", 1 + RNG.nextInt(9));
            nodes.add(nd);
        }
        root.set("nodes", nodes);

        ArrayNode edges = om.createArrayNode();
        for (int u = 0; u < n; u++) {
            for (int v = u+1; v < n; v++) {
                if (RNG.nextDouble() < density) edges.add(edge(om, "T"+u, "T"+v));
            }
        }
        if (cycles && n >= 4) {
            int cnum = 1 + RNG.nextInt(3);
            for (int c = 0; c < cnum; c++) {
                int a = RNG.nextInt(n-3);
                int b = a+1+RNG.nextInt(2);
                int d = b+1+RNG.nextInt(2);
                edges.add(edge(om, "T"+a, "T"+b));
                edges.add(edge(om, "T"+b, "T"+d));
                edges.add(edge(om, "T"+d, "T"+a));
            }
        }
        root.set("edges", edges);
        return root;
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper om = new ObjectMapper();
        new File("data").mkdirs();

        int[][] small = {{6,0},{8,1},{10,0}}; double[] sD = {0.25, 0.4, 0.35};
        int[] medN = {12, 16, 20};           double[] mD = {0.25, 0.35, 0.45};
        int[] bigN = {25, 35, 50};           double[] bD = {0.2, 0.3, 0.4};

        for (int i = 0; i < 3; i++) {
            ObjectNode g = makeGraph(small[i][0], sD[i], small[i][1]==1, "small_"+(i+1),
                    small[i][1]==1 ? "Small mixed with cycles" : "Small DAG");
            om.writerWithDefaultPrettyPrinter().writeValue(new File("data/small_"+(i+1)+".json"), g);
        }
        for (int i = 0; i < 3; i++) {
            ObjectNode g = makeGraph(medN[i], mD[i], true, "medium_"+(i+1), "Medium mixed with several SCCs");
            om.writerWithDefaultPrettyPrinter().writeValue(new File("data/medium_"+(i+1)+".json"), g);
        }
        for (int i = 0; i < 3; i++) {
            ObjectNode g = makeGraph(bigN[i], bD[i], i!=0, "large_"+(i+1), i==0 ? "Large mostly DAG" : "Large mixed");
            om.writerWithDefaultPrettyPrinter().writeValue(new File("data/large_"+(i+1)+".json"), g);
        }
        System.out.println("Datasets written to ./data");
    }
}
