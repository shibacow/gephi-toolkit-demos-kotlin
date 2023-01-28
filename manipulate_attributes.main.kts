#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/ManipulateAttributes.java
 */

import java.io.File;
import java.io.IOException;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

//Init a project - and therefore a workspace
val pc = Lookup.getDefault().lookup(ProjectController::class.java);
pc.newProject();
val workspace = pc.getCurrentWorkspace();

//Get controllers and models
val importController = Lookup.getDefault().lookup(ImportController::class.java);

//Import file
val imc = Lookup.getDefault().lookup(ImportController::class.java)
var filename="resource/polblogs.gml"
try{
    val file = File(filename)
    val container = imc.importFile(file)
    container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED)
    container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
    imc.process(container,DefaultProcessor(),workspace)
}catch(ex: IOException){
    ex.printStackTrace()
}

//List node columns
val model = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel();
for (col in model.getNodeTable()) {
    println(col);
}

//Add boolean column
val testCol = model.getNodeTable().addColumn("test", Boolean::class.java);

//Write values to nodes
for (node in model.getGraph().getNodes()) {
    node.setAttribute(testCol, true);
}

//Iterate values - fastest
val sourceCol = model.getNodeTable().getColumn("source");
for (n in model.getGraph().getNodes()) {
    println(n.getAttribute(sourceCol));
}

//Iterate values - normal
for (n in model.getGraph().getNodes()) {
   println(n.getAttribute("source"));
}
