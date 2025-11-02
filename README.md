## Report

### Data summary (sizes & weight model)

* Weight model: node durations. Path cost is the sum of node times. After SCC compression a component’s time is the sum of its tasks.
* Datasets: nine JSON files under `/data` across small / medium / large. Some are pure DAGs, some have cycles, density varies.

|  Dataset | n (nodes) | #SCC | ~E (edges) | Structure                   |
| -------: | --------: | ---: | ---------: | --------------------------- |
|  small_1 |         6 |    6 |          2 | pure DAG (all SCC size = 1) |
|  small_2 |         8 |    6 |         12 | cyclic (has a 3-node SCC)   |
|  small_3 |        10 |   10 |         15 | pure DAG                    |
| medium_1 |        12 |   10 |         13 | cyclic (has a 3-node SCC)   |
| medium_2 |        16 |    9 |         43 | cyclic (has a 6-node SCC)   |
| medium_3 |        20 |   15 |         91 | cyclic (multiple SCCs)      |
|  large_1 |        25 |   25 |         56 | pure DAG                    |
|  large_2 |        35 |   28 |        195 | cyclic (several SCCs)       |
|  large_3 |        50 |   44 |        486 | cyclic (large & multiple)   |

`~E` is a quick estimate from SCC metrics: `scc_edge_traversals / 2`.

### Results

#### SCC (Kosaraju) — metrics and time

|  Dataset | Max SCC size | scc_dfs_visits | scc_edge_traversals | scc_time_ns |
| -------: | -----------: | -------------: | ------------------: | ----------: |
|  small_1 |            1 |             12 |                   4 |     473,300 |
|  small_2 |            3 |             16 |                  24 |     732,200 |
|  small_3 |            1 |             20 |                  30 |     506,300 |
| medium_1 |            3 |             24 |                  26 |     422,000 |
| medium_2 |            6 |             32 |                  86 |     838,100 |
| medium_3 |            4 |             40 |                 182 |     722,300 |
|  large_1 |            1 |             50 |                 112 |     416,100 |
|  large_2 |            4 |             70 |                 390 |     925,400 |
|  large_3 |            5 |            100 |                 972 |   1,157,000 |

Reference: `scc_dfs_visits = 2 × n`, `scc_edge_traversals ≈ 2 × E`.

#### Topological ordering (Kahn) — metrics and time

|  Dataset | Topo length (= #SCC) | kahn_pushes | kahn_pops | topo_time_ns |
| -------: | -------------------: | ----------: | --------: | -----------: |
|  small_1 |                    6 |           6 |         6 |       35,900 |
|  small_2 |                    6 |           6 |         6 |       46,700 |
|  small_3 |                   10 |          10 |        10 |       59,600 |
| medium_1 |                   10 |          10 |        10 |      108,700 |
| medium_2 |                    9 |           9 |         9 |       68,500 |
| medium_3 |                   15 |          15 |        15 |      105,700 |
|  large_1 |                   25 |          25 |        25 |      101,300 |
|  large_2 |                   28 |          28 |        28 |      143,900 |
|  large_3 |                   44 |          44 |        44 |      181,600 |

In every case: `Topo length == #SCC` and `pushes == pops == |V_DAG|`.

#### DAG shortest paths (node-weighted) and critical path

|  Dataset | Source (component) | Critical path length | dag_relax_attempts | dag_relax_success |
| -------: | ------------------ | -------------------: | -----------------: | ----------------: |
|  small_1 | C5                 |                    9 |                  — |                 — |
|  small_2 | C2                 |                   31 |                  4 |                 3 |
|  small_3 | C5                 |                   28 |                  5 |                 4 |
| medium_1 | C4                 |                   34 |                  6 |                 5 |
| medium_2 | C0                 |                   67 |                 21 |                 8 |
| medium_3 | C0                 |                  108 |                 61 |                16 |
|  large_1 | C17                |                   29 |                  9 |                 7 |
|  large_2 | C9                 |                   92 |                 74 |                24 |
|  large_3 | C3                 |                  177 |                374 |                86 |

If a printed “one optimal shortest path” is empty, that source and target just don’t connect in that run. Easiest fix for demos: source = first topo component, target = last.

### Analysis

* On cyclic graphs SCC does the heavy lifting. Bigger components and higher density bump up `scc_edge_traversals` and `scc_time_ns` before scheduling even starts.
* Condensation pays off. After we shrink to components, Kahn is basically linear, and the queue stats match the number of components.
* DAG shortest/longest depends on how dense and “long” the condensation DAG is. More edges and a longer chain mean more relaxations and a longer critical path (see large_2, large_3).

### Conclusions

* First compress cycles (Kosaraju or Tarjan), then plan on the condensation DAG.
* Kahn is a good default for topological order: simple, predictable, easy to read in metrics.
* On the DAG, the node-duration DP gives both shortest paths and the critical path with straightforward path reconstruction.
* For clean, reproducible runs set source to the first topo node and target to the last so a shortest path definitely exists.
