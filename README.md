# Gephi Toolkit Demos By Using Kotolin

The Gephi Toolkit project package essential modules (Graph, Layout, Filters, IO...) in a standard Java library, which any Java project can use for getting things done.

Java tutorial site using Gephi-toolkit is [there](https://github.com/gephi/gephi-toolkit-demos).

This project rewrite above site  using kotlin.

# List of rewritten Java projects in Gephi-toolkit-demos

The java file in the above demo was converted to the following kts file.

- HeadlessSimple.java -> headless_sample.main.kts
- WithAutoLayout.java -> auto_layout.main.kts
- ParallelWorkspace.java -> parallel_workspace.main.kts
- PartitionGraph.java -> partition_graph.main.kts 
- RankingGraph.java -> ranking_graph.main.kts
- Filtering.java -> filtering.main.kts
- ImportExport.java -> import_export.main.kts
- SQLiteImportExport.java -> sqlite_import_export.main.kts
- ManualGraph.java -> manual_graph.main.kts
- ManipulateAttributes.java -> manipulate_attributes.main.kts
- DynamicMetric.java -> dynamic_metric.main.kts
- ImportDynamic.java -> import_dynamic.main.kts

# Use this demos

setup kotlin.

run `kotlin headless_sample.main.kts`

## simple demo

use run.sh

```
./run.sh
```

## Notice

- A large number of nodes consumes a large amount of memory.
- Pay attention to access privileges


The following environment variables were set when the above kotlin was run.

```
export MEM_SIZE=6
export JDK_JAVA_OPTIONS="-Xms${MEM_SIZE}g -Xmx${MEM_SIZE}g --add-opens=java.base/java.net=ALL-UNNAMED"
```
## pitfall

In partition_graph.main.kts, I read `palette_default.csv` ,`palette_presets.csv` but getting permission error due to enhanced java security.
Therefore, `--add-opens=java.base/java.net=ALL-UNNAMED` is required in `JDK_JAVA_OPTIONS`.

# References:

See [website](https://gephi.org/toolkit/)
Source [code](https://github.com/gephi/gephi-toolkit)
