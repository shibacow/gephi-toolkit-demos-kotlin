#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")
/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/DynamicMetric.java
*/

import java.util.Random;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Interval;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.types.TimestampDoubleMap;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.api.StatisticsController;
import org.gephi.statistics.plugin.dynamic.DynamicDegree;
import org.openide.util.Lookup;


val pc = Lookup.getDefault().lookup(ProjectController::class.java);
pc.newProject();
val workspace = pc.getCurrentWorkspace();

//Generate a new random graph into a container
val container = Lookup.getDefault().lookup(Container.Factory::class.java).newContainer();
val randomGraph = RandomGraph();
randomGraph.setNumberOfNodes(500);
randomGraph.setWiringProbability(0.005);
randomGraph.generate(container.getLoader());

//Append container to graph structure
val importController = Lookup.getDefault().lookup(ImportController::class.java);
importController.process(container, DefaultProcessor(), workspace);

//Add a fake 'Date' column to nodes
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace);

//Add a random date to all nodes - between 1990 and 2010
val graph = graphModel.getGraph();
val random = Random();
for (n in graph.getNodes()) {
    val randomDataValue:Double = (random.nextInt(21)  + 1990).toDouble();
    n.addInterval(Interval(randomDataValue, Double.POSITIVE_INFINITY));
}

//Execute metric
val statisticsController = Lookup.getDefault().lookup(StatisticsController::class.java);
val degree = DynamicDegree();
degree.setWindow(1.0);
degree.setTick(1.0);
degree.setBounds(graphModel.getTimeBounds());
statisticsController.execute(degree);

//Get averages
println("Average degree:");
val averages:TimestampDoubleMap = graph.getAttribute(DynamicDegree.DYNAMIC_AVGDEGREE) as TimestampDoubleMap;
for ( t in averages.getTimestamps()) {
    println("${t} -> ${averages.getDouble(t)}");
}
