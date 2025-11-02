## Report

### Data summary (sizes & weight model)

* **Weight model:** node durations. Path cost is the sum of node durations. In the condensation DAG, a component’s duration equals the sum of its member tasks.
* **Datasets:** nine graphs under `/data` (small/medium/large), mixing pure DAGs and cyclic inputs with varied densities.

| Dataset  | n (nodes) | #SCC | ~E (edges) | Structure                      |
| -------- | --------: | ---: | ---------: | ------------------------------ |
| small_1  |         6 |    6 |          2 | Pure DAG (all SCC size = 1)    |
| small_2  |         8 |    6 |         12 | Cyclic (has 3-node SCC)        |
| small_3  |        10 |   10 |         15 | Pure DAG                       |
| medium_1 |        12 |   10 |         13 | Cyclic (has 3-node SCC)        |
| medium_2 |        16 |    9 |         43 | Cyclic (has 6-node SCC)        |
| medium_3 |        20 |   15 |         91 | Cyclic (multiple SCCs)         |
| large_1  |        25 |   25 |         56 | Pure DAG                       |
| large_2  |        35 |   28 |        195 | Cyclic (several SCCs)          |
| large_3  |        50 |   44 |        486 | Cyclic (large & multiple SCCs) |

`~E` is estimated from SCC metrics: `~E ≈ scc_edge_traversals / 2`.

---

### Results

#### SCC (Kosaraju): metrics & time

| Dataset  | Max SCC size | scc_dfs_visits | scc_edge_traversals | scc_time_ns |
| -------- | -----------: | -------------: | ------------------: | ----------: |
| small_1  |            1 |             12 |                   4 |     473,300 |
| small_2  |            3 |             16 |                  24 |     732,200 |
| small_3  |            1 |             20 |                  30 |     506,300 |
| medium_1 |            3 |             24 |                  26 |     422,000 |
| medium_2 |            6 |             32 |                  86 |     838,100 |
| medium_3 |            4 |             40 |                 182 |     722,300 |
| large_1  |            1 |             50 |                 112 |     416,100 |
| large_2  |            4 |             70 |                 390 |     925,400 |
| large_3  |            5 |            100 |                 972 |   1,157,000 |

Reference relations: `scc_dfs_visits = 2 × n` (two DFS passes). `scc_edge_traversals ≈ 2 × E`.

#### Topological ordering (Kahn): metrics & time

| Dataset  | Topo length (= #SCC) | kahn_pushes | kahn_pops | topo_time_ns |
| -------- | -------------------: | ----------: | --------: | -----------: |
| small_1  |                    6 |           6 |         6 |       35,900 |
| small_2  |                    6 |           6 |         6 |       46,700 |
| small_3  |                   10 |          10 |        10 |       59,600 |
| medium_1 |                   10 |          10 |        10 |      108,700 |
| medium_2 |                    9 |           9 |         9 |       68,500 |
| medium_3 |                   15 |          15 |        15 |      105,700 |
| large_1  |                   25 |          25 |        25 |      101,300 |
| large_2  |                   28 |          28 |        28 |      143,900 |
| large_3  |                   44 |          44 |        44 |      181,600 |

For every dataset: `Topo length == #SCC` and `pushes == pops == |V_DAG|`.

#### DAG shortest paths (node-weighted) and critical (longest) path

| Dataset  | Source (component) | Critical path length | dag_relax_attempts | dag_relax_success |
| -------- | ------------------ | -------------------: | -----------------: | ----------------: |
| small_1  | C5                 |                    9 |                  — |                 — |
| small_2  | C2                 |                   31 |                  4 |                 3 |
| small_3  | C5                 |                   28 |                  5 |                 4 |
| medium_1 | C4                 |                   34 |                  6 |                 5 |
| medium_2 | C0                 |                   67 |                 21 |                 8 |
| medium_3 | C0                 |                  108 |                 61 |                16 |
| large_1  | C17                |                   29 |                  9 |                 7 |
| large_2  | C9                 |                   92 |                 74 |                24 |
| large_3  | C3                 |                  177 |                374 |                86 |

If the printed “one optimal shortest path” is empty, the chosen target was unreachable from the chosen source in that specific run. For reproducible, non-empty shortest paths: pick source = first topo component and target = last topo component.


### Analysis

* **SCC is the heavy step on cyclic inputs.** Larger SCCs and higher density increase `scc_edge_traversals` and `scc_time_ns` before compression (medium_2, large_2, large_3).
* **Condensation shrinks the problem.** After SCC compression, the DAG size equals the number of components, so Kahn stays linear with `pushes == pops == #SCC`.
* **DAG-SP reflects density and chain length.** As the condensation DAG gets denser and longer, relaxation counts and critical-path length grow (large_2, large_3).


### Conclusions

* Compress cycles first (Kosaraju/Tarjan), then schedule on the condensation DAG.
* Use Kahn for topological ordering: simple, linear, easy to instrument.
* On the DAG, node-weighted DP yields both shortest paths and the critical path with straightforward reconstruction.
* For demos and grading, set source to the first topo component and target to the last to guarantee a reachable shortest path.
