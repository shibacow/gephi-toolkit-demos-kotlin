#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/ImportDynamic.java
*/

import java.io.File;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.attribute.AttributeRangeBuilder;
import org.gephi.filters.plugin.dynamic.DynamicRangeBuilder.DynamicRangeFilter;
import org.gephi.graph.api.*;
import org.gephi.graph.api.types.TimestampIntegerMap;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.MergeProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

val pc = Lookup.getDefault().lookup(ProjectController::class.java);
pc.newProject();
val workspace = pc.getCurrentWorkspace();
val importController = Lookup.getDefault().lookup(ImportController::class.java);

val containers: MutableList<Container> = mutableListOf();

try {
    for (i in 1..3 ) {
        //val file = new File(getClass().getResource("/org/gephi/toolkit/demos/timeframe" + (i + 1) + ".gexf").toURI());
        val fname="resource/timeframe${i}.gexf";
        val file = File(fname)
        containers.add(importController.importFile(file));
    }
} catch (ex:Exception) {
    ex.printStackTrace();
}
importController.process(containers.toList().toTypedArray(),MergeProcessor(), workspace);

//Get the price attribute
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel();
val graph = graphModel.getGraph();
for (n in graph.getNodes()) {
    val value:TimestampIntegerMap  = n.getAttribute("price") as TimestampIntegerMap;
    println("${n.getLabel()} : ${value.toString()}");
}

//Get the price attribute in average - learn more about ESTIMATOR
for (n in graph.getNodes()) {
    val value:TimestampIntegerMap  = n.getAttribute("price") as TimestampIntegerMap;

    val priceFrom2007to2009Avg = value.get(Interval(2007.toDouble(), 2009.toDouble()), Estimator.AVERAGE)
    println("With AVERAGE estimator: ${n.getLabel()} : ${priceFrom2007to2009Avg}");

    val priceFrom2007to2009Max = value.get(Interval(2007.toDouble(), 2009.toDouble()), Estimator.MAX);
    println("With MAX estimator: ${n.getLabel()} : ${priceFrom2007to2009Max}");
}

//Create a dynamic range filter query
val filterController = Lookup.getDefault().lookup(FilterController::class.java);
val dynamicRangeFilter = DynamicRangeFilter(graphModel);
val dynamicQuery = filterController.createQuery(dynamicRangeFilter);


//Create a attribute range filter query - on the price column
val priceCol = graphModel.getNodeTable().getColumn("price");
val attributeRangeFilter = AttributeRangeBuilder.AttributeRangeFilter.Node(priceCol);
val priceQuery = filterController.createQuery(attributeRangeFilter);


//Set dynamic query as child of price query
filterController.add(priceQuery);
filterController.add(dynamicQuery);
filterController.setSubQuery(priceQuery, dynamicQuery);

//Set the filters parameters - Keep nodes between 2007-2008 which have average price >= 7
dynamicRangeFilter.setRange(Range(2007.0, 2008.0));
attributeRangeFilter.setRange(Range(7, Integer.MAX_VALUE));



//Execute the filter query
val view = filterController.filter(priceQuery);
val filteredGraph = graphModel.getGraph(view);

//Node 3 shoudln't be in this graph
println("Node 3 in the filtered graph: ${filteredGraph.contains(graph.getNode("3"))}");
