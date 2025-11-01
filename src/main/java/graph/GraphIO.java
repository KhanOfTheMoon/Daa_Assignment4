package graph;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import java.io.File;
import java.util.*;

public class GraphIO {
    public static Graph loadJson(String path) throws Exception {
        ObjectMapper om = new ObjectMapper();
        JsonNode root = om.readTree(new File(path));

        List<String> ids = new ArrayList<>();
        List<Integer> durations = new ArrayList<>();
        Map<String,Integer> idToIdx = new HashMap<>();

        ArrayNode nodes = (ArrayNode) root.get("nodes");
        for (int i = 0; i < nodes.size(); i++) {
            JsonNode nd = nodes.get(i);
            String id = nd.get("id").asText();
            int dur = nd.get("duration").asInt();
            idToIdx.put(id, i);
            ids.add(id);
            durations.add(dur);
        }
        int[] durArr = durations.stream().mapToInt(Integer::intValue).toArray();

        List<int[]> edges = new ArrayList<>();
        ArrayNode es = (ArrayNode) root.get("edges");
        for (int i = 0; i < es.size(); i++) {
            JsonNode e = es.get(i);
            int u = idToIdx.get(e.get("from").asText());
            int v = idToIdx.get(e.get("to").asText());
            edges.add(new int[]{u, v});
        }
        return new Graph(ids, durArr, edges);
    }
}
