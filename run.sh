#!/bin/bash
set -ue

echo -e "start \t`date`"
#export JDK_JAVA_OPTIONS="-Xms4g -Xmx4g"
export MEM_SIZE=6
export JDK_JAVA_OPTIONS="-Xms${MEM_SIZE}g -Xmx${MEM_SIZE}g --add-opens=java.base/java.net=ALL-UNNAMED"
echo "headless_sample.main.kts" && kotlin headless_sample.main.kts
echo "auto_layout.main.kts" && kotlin auto_layout.main.kts
echo "partition_graph.main.kts" && kotlin partition_graph.main.kts
echo "ranking_graph.main.kts" && kotlin ranking_graph.main.kts
echo "filtering.main.kts" && kotlin filtering.main.kts
echo "import_export.main.kts" && kotlin import_export.main.kts
echo "sqlite_import_export.main.kts" && kotlin sqlite_import_export.main.kts
echo "manual_graph.main.kts" && kotlin manual_graph.main.kts
echo "manipulate_attributes.main.kts" && kotlin manipulate_attributes.main.kts
echo "dynamic_metric.main.kts" && kotlin dynamic_metric.main.kts
echo "import_dynamic.main.kts" && kotlin import_dynamic.main.kts
echo -e "finish \t`date`"