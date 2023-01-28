#!/bin/bash

#export JDK_JAVA_OPTIONS="-Xms4g -Xmx4g"
export JDK_JAVA_OPTIONS="-Xms4g -Xmx4g --add-opens=java.base/java.net=ALL-UNNAMED"
#kotlin headless_sample.main.kts
#kotlin appearance.main.kts
#kotlin auto_layout.main.kts
#kotlin partition_graph.main.kts
#kotlin ranking_graph.main.kts
#kotlin filtering.main.kts
#kotlin import_export.main.kts
#kotlin sqlite_import_export.main.kts
#kotlin manual_graph.main.kts
kotlin manipulate_attributes.main.kts
