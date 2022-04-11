#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.9.3-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/HeadlessSimple.java
 */

import org.gephi.project.api.ProjectController
import org.gephi.io.importer.api.ImportController
import org.gephi.io.exporter.api.ExportController
import org.gephi.graph.api.GraphController
import org.gephi.io.processor.plugin.DefaultProcessor
import org.openide.util.Lookup
import java.io.File
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout
import org.gephi.layout.plugin.force.StepDisplacement
import java.util.concurrent.TimeUnit
import org.gephi.io.importer.api.Container
import org.gephi.io.generator.plugin.RandomGraph
import org.gephi.layout.plugin.AutoLayout


//Init a project - and therefore a workspace

val pc = Lookup.getDefault().lookup(ProjectController::class.java)
pc.newProject()
val workspace = pc.getCurrentWorkspace()


//Generate a new random graph into a container
val container = Lookup.getDefault().lookup(Container.Factory::class.java).newContainer()
val randomGraph = RandomGraph()
randomGraph.setNumberOfNodes(500);
randomGraph.setWiringProbability(0.005);
randomGraph.generate(container.getLoader());

//Append container to graph structure
val importController = Lookup.getDefault().lookup(ImportController::class.java);
importController.process(container, DefaultProcessor(), workspace)

//See if graph is well imported
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel()
val graph = graphModel.getDirectedGraph()
println("Nodes: " + graph.getNodeCount())
println("Edges: " + graph.getEdgeCount())

//Layout for 1 minute
val autoLayout = AutoLayout(1, TimeUnit.MINUTES);
autoLayout.setGraphModel(graphModel);

val firstLayout = YifanHuLayout(null, StepDisplacement(1f));

val secondLayout = ForceAtlasLayout(null);
val adjustBySizeProperty:AutoLayout.DynamicProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", true, 0.1f); //True after 10% of layout time
val repulsionProperty:AutoLayout.DynamicProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 500.0, 0f); //500 for the complete period

autoLayout.addLayout(firstLayout, 0.5f);
val propaties = listOf(adjustBySizeProperty, repulsionProperty).toTypedArray()

autoLayout.addLayout(secondLayout, 0.5f, propaties)
try{
        autoLayout.execute()
}catch(e:IllegalArgumentException){
        println("error")
}

//Export
 
val ec = Lookup.getDefault().lookup(ExportController::class.java)
ec.exportFile(File("autolayout.pdf"))
