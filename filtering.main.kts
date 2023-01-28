#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/Filtering.java
*/

import java.io.File;
import java.io.IOException;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Partition;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.filters.plugin.graph.EgoBuilder.EgoFilter;
import org.gephi.filters.plugin.operator.INTERSECTIONBuilder.IntersectionOperator;
import org.gephi.filters.plugin.partition.PartitionBuilder.NodePartitionFilter;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

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

//Filter, remove degree < 10
val filterController = Lookup.getDefault().lookup(FilterController::class.java);
val degreeFilter = DegreeRangeFilter();
degreeFilter.init(graphModel.getGraph());
degreeFilter.setRange(Range(10, Integer.MAX_VALUE));     //Remove nodes with degree < 10
val query = filterController.createQuery(degreeFilter);
val view = filterController.filter(query);
graphModel.setVisibleView(view);    //Set the filter result as the visible view

//Count nodes and edges on filtered graph
val graph_visible = graphModel.getDirectedGraphVisible();

println("Nodes: " + graph_visible.getNodeCount() + " Edges: " + graph_visible.getEdgeCount());

//This part of the program could not be kotolinized due to an error.

/*
        //Filter, keep partition 'Blogarama'. Build partition with 'source' column in the data
        NodePartitionFilter partitionFilter = new NodePartitionFilter(appearanceModel.getNodePartition(graphModel.getNodeTable().getColumn("source")));
        partitionFilter.unselectAll();
        partitionFilter.addPart("Blogarama");
        Query query2 = filterController.createQuery(partitionFilter);
        GraphView view2 = filterController.filter(query2);
        graphModel.setVisibleView(view2);    //Set the filter result as the visible view

        //Count nodes and edges on filtered graph
        graph = graphModel.getDirectedGraphVisible();
        System.out.println("Nodes: " + graph.getNodeCount() + " Edges: " + graph.getEdgeCount());
 */

//Combine two filters with AND - Set query and query2 as sub-query of AND
val intersectionOperator = IntersectionOperator();
val query3 = filterController.createQuery(intersectionOperator);
filterController.setSubQuery(query3, query);
//filterController.setSubQuery(query3, query2);
val view3 = filterController.filter(query3);
graphModel.setVisibleView(view3);    //Set the filter result as the visible view

//Count nodes and edges on filtered graph
val graph3 = graphModel.getDirectedGraphVisible();
println("Nodes: " + graph3.getNodeCount() + " Edges: " + graph3.getEdgeCount());

//Ego filter
val egoFilter = EgoFilter();
egoFilter.setPattern("obamablog.com"); //Regex accepted
egoFilter.setDepth(1);
val queryEgo = filterController.createQuery(egoFilter);
val viewEgo = filterController.filter(queryEgo);
graphModel.setVisibleView(viewEgo);    //Set the filter result as the visible view

//Count nodes and edges on filtered graph
val graph_ego = graphModel.getDirectedGraphVisible();
System.out.println("Nodes: " + graph_ego.getNodeCount() + " Edges: " + graph_ego.getEdgeCount());