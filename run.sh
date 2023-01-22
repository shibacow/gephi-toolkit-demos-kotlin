#!/bin/bash

export JDK_JAVA_OPTIONS="-Xms1g -Xmx1g --add-opens=java.base/java.net=ALL-UNNAMED"
#kotlin headless_sample.main.kts
#kotlin appearance.main.kts
#kotlin auto_layout.main.kts
kotlin partition_graph.main.kts
