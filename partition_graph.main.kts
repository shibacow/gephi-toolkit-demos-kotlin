#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")
/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/PartitionGraph.java
 */

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;


//Init a project - and therefore a workspace

val pc = Lookup.getDefault().lookup(ProjectController::class.java);
pc.newProject();
val workspace = pc.getCurrentWorkspace();

//Get controllers and models
val importController = Lookup.getDefault().lookup(ImportController::class.java);
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel();
val appearanceController = Lookup.getDefault().lookup(AppearanceController::class.java);
val appearanceModel = appearanceController.getModel();


val imc = Lookup.getDefault().lookup(ImportController::class.java)
val file = File("polblogs.gml")
val container = imc.importFile(file)
container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED)
imc.process(container,DefaultProcessor(),workspace)

//See if graph is well imported
val graph = graphModel.getDirectedGraph()
println("Nodes: " + graph.getNodeCount())
println("Edges: " + graph.getEdgeCount())

//Partition with 'source' column, which is in the data
val column = graphModel.getNodeTable().getColumn("source");

val clusterPartitionFunction = appearanceModel.getNodeFunction(
	column,
	PartitionElementColorTransformer::class.java
)
val partition = (clusterPartitionFunction as PartitionFunction).partition
println("partition.size="+partition.size(graph))
val palette = PaletteManager.getInstance().generatePalette(partition.size(graph))
partition.setColors(graph, palette.getColors());
appearanceController.transform(clusterPartitionFunction)
val ec = Lookup.getDefault().lookup(ExportController::class.java)
println("ec="+ec)
try{
	ec.exportFile(File("pdf/partition1.pdf"));
} catch (ex: IOException) {
	ex.printStackTrace()
}