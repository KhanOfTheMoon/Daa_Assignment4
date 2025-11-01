package graph;

import java.util.*;

public class Metrics {
    private final Map<String,Long> counters = new HashMap<>();
    private final Map<String,Long> timers = new HashMap<>();
    private final Map<String,Long> starts = new HashMap<>();

    public void inc(String name, long by) { counters.merge(name, by, Long::sum); }
    public void startTimer(String name) { starts.put(name, System.nanoTime()); }
    public void stopTimer(String name) {
        long st = starts.getOrDefault(name, 0L);
        long dt = System.nanoTime() - st;
        timers.merge(name, dt, Long::sum);
    }
    public long get(String name) { return counters.getOrDefault(name, 0L) + timers.getOrDefault(name, 0L); }
    public Map<String,Long> all() {
        Map<String,Long> out = new LinkedHashMap<>();
        out.putAll(counters);
        timers.forEach((k,v) -> out.put(k+"_ns", v));
        return out;
    }
}
