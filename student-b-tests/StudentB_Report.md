# Student B — Phase 2 implementation notes

## Scope
Implementation lives in the repository-root `RideSharingSystem.java`, which implements `IRideSharingSystem` and uses the custom `BST` and `LinkedList` from `code/`. The nine indexes are grouped into three for riders, three for drivers, and three for rides. Indexed rider/driver operations and CSV loading are Student B's responsibility. The ride methods are placeholders pending Student C.

## Question 1 — Complexity
Let `n` be riders, `m` drivers, `k` matching returned records, and `h` tree height. On an unbalanced BST, `h` may reach `n`; with a balanced tree, `h = O(log n)`. Key comparison is assumed O(1).

| Method group | Unbalanced worst case | Balanced BST |
|---|---|---|
| Rider/driver lookup by ID | O(n) / O(m) | O(log n) / O(log m) |
| Search by rider name/city or driver type | O(n+k) / O(m+k) | O(log n+k) / O(log m+k) |
| Add rider/driver (update all indexes) | O(n) / O(m) | O(log n) / O(log m) |
| Return all riders/drivers sorted by ID | O(n) / O(m) | O(n) / O(m) |
| `nbKeyCompRiderById` | O(n) | O(log n) |
| `indexSize` | O(1) | O(1) |
| `indexHeight` | O(u) for u outer nodes | O(u) |
| Load n riders / m drivers from CSV | O(n²) / O(m²) | O(n log n) / O(m log m) |

Removing a rider or driver without scheduled rides requires indexed deletion, O(n) or O(m) worst case. If scheduled rides exist, deletion costs also depend on the number of affected rides and Student C's cancellation implementation. A precise final bound requires integration with C's code. Compared with Phase 1's linked-list searches, BST lookup can improve from linear to logarithmic **when the tree is balanced**, but the provided BST is not self-balancing.

## Question 2 — Nested BST indexes
For `ridersByName`, the outer BST key is a rider name and its value is an inner BST keyed by unique integer rider IDs. If d distinct names exist and k riders share the searched name, searching the outer BST costs O(H) and returning the k riders via inner in-order traversal costs O(k): total O(H+k). In the worst skewed case this becomes O(d+k), and in a balanced outer BST O(log d+k). Deleting a rider requires removing their ID from the inner BST and deleting the outer key if the inner tree becomes empty; these operations preserve index consistency. Other nested indexes use the same structure.

Using a linked list instead of an inner BST requires up to O(k) steps to find/remove a particular ID, while a balanced inner BST takes O(log k). An unbalanced inner BST may also take O(k), so improvement is not guaranteed.

## Question 3 — Actual tree shapes
These figures come from loading `riders_100.csv` and `drivers_30.csv` into an initially empty system and running `indexSize/indexHeight`. Height is counted in **nodes**, as specified by `IBST`.

| Index | Size | Height |
|---|---:|---:|
| RIDERS_BY_ID | 100 | 100 |
| RIDERS_BY_NAME | 96 | 13 |
| RIDERS_BY_HOME_CITY | 28 | 11 |
| DRIVERS_BY_ID | 30 | 30 |
| DRIVERS_BY_PLATE | 30 | 7 |
| DRIVERS_BY_VEHICLE_TYPE | 4 | 3 |

The ID trees have a chain-shaped structure because the imported records have IDs in ascending order; search comparisons for rider IDs 1, 50, 100, and absent 101 are respectively 1, 50, 100, and 100. The three ride indexes remain empty until Student C's implementation and ride CSV import are integrated.

## Verify locally
Run from the repository root:
```sh
javac -d out code/*.java RideSharingSystem.java student-b-tests/TestB.java
java -cp out TestB
```

**Integration warning:** the ten Student C methods in `RideSharingSystem.java` currently throw `UnsupportedOperationException`. Rider/driver deletion tests here use people without scheduled rides; cancellation and real cascading deletion cannot be verified until Student C completes those methods.
