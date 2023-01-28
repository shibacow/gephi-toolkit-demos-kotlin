#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/RankingGraph.java
 */

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
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

//Import file
val imc = Lookup.getDefault().lookup(ImportController::class.java)
var filename="resource/polblogs.gml"
filename="resource/example.gml"
try{
    val file = File(filename)
    val container = imc.importFile(file)
    container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED)
    imc.process(container,DefaultProcessor(),workspace)
}catch(ex: IOException){
    ex.printStackTrace()
}

//See if graph is well imported
val graph = graphModel.getDirectedGraph()
println("Nodes: " + graph.getNodeCount())
println("Edges: " + graph.getEdgeCount())

//Rank color by Degree
val degreeRanking = appearanceModel.getNodeFunction(graphModel.defaultColumns()
            .degree(), RankingElementColorTransformer::class.java);

val degreeTransformer: RankingElementColorTransformer = degreeRanking.getTransformer();
val colors = listOf(Color(0xFEF0D9),Color(0xB30000))
degreeTransformer.setColors(colors.toTypedArray());
degreeTransformer.setColorPositions(arrayOf(0f,1f).toFloatArray());
appearanceController.transform(degreeRanking);

//Get Centrality
val distance = GraphDistance();
distance.setDirected(true);
distance.execute(graphModel);


//Rank size by centrality
val centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
val centralityRanking = appearanceModel.getNodeFunction(centralityColumn, RankingNodeSizeTransformer::class.java);
val centralityTransformer: RankingNodeSizeTransformer = centralityRanking.getTransformer();
centralityTransformer.setMinSize(3f);
centralityTransformer.setMaxSize(10f);
appearanceController.transform(centralityRanking);

//Rank label size - set a multiplier size
val centralityRanking2 = appearanceModel.getNodeFunction(centralityColumn, RankingLabelSizeTransformer::class.java);
val labelSizeTransformer: RankingLabelSizeTransformer = centralityRanking2.getTransformer();
labelSizeTransformer.setMinSize(1f);
labelSizeTransformer.setMaxSize(3f);
appearanceController.transform(centralityRanking2);


//Set 'show labels' option in Preview - and disable node size influence on text size
val previewModel = Lookup.getDefault().lookup(PreviewController::class.java).getModel();
previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, true);
previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, false);

val ec = Lookup.getDefault().lookup(ExportController::class.java)
println("ec="+ec)
 //Export
try{
	ec.exportFile(File("pdf/ranking_graph.svg"));
} catch (ex: IOException) {
	ex.printStackTrace()
}