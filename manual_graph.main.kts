#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/ManualGraph.java
 */

import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

//Init a project - and therefore a workspace
val pc = Lookup.getDefault().lookup(ProjectController::class.java);
pc.newProject();
val workspace = pc.getCurrentWorkspace();

//Get a graph model - it exists because we have a workspace
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace);

//Create three nodes
val n0 = graphModel.factory().newNode("n0");
n0.setLabel("Node 0");
val n1 = graphModel.factory().newNode("n1");
n1.setLabel("Node 1");
val n2 = graphModel.factory().newNode("n2");
n2.setLabel("Node 2");

//Create three edges
val e1 = graphModel.factory().newEdge(n1, n2, 0, 1.0, true);
val e2 = graphModel.factory().newEdge(n0, n2, 0, 2.0, true);
val e3 = graphModel.factory().newEdge(n2, n0, 0, 2.0, true);   //This is e2's mutual edge

//Append as a Directed Graph
val directedGraph = graphModel.getDirectedGraph();
directedGraph.addNode(n0);
directedGraph.addNode(n1);
directedGraph.addNode(n2);
directedGraph.addEdge(e1);
directedGraph.addEdge(e2);
directedGraph.addEdge(e3);

//Count nodes and edges
println("Nodes: ${directedGraph.getNodeCount()} \tEdges: ${directedGraph.getEdgeCount()}");

val undirectedGraph = graphModel.getUndirectedGraph();
println("Edges: ${undirectedGraph.getEdgeCount()}");   //The mutual edge is automatically merged

//Iterate over nodes
for (n in directedGraph.getNodes()) {
    val neighbors = directedGraph.getNeighbors(n).toArray();
    println("${n.getLabel()} has ${neighbors.size} neighbors");
}

//Iterate over edges
for (e in directedGraph.getEdges()) {
    println("${e.getSource().getId()}   ${e.getTarget().getId()}");
}

//Find node by id
val node2 = directedGraph.getNode("n2");

//Get degree
println("Node2 degree: ${directedGraph.getDegree(node2)}");

//Modify the graph while reading
//Due to locking, you need to use toArray() on Iterable to be able to modify
//the graph in a read loop
for (n in directedGraph.getNodes().toArray()) {
    directedGraph.removeNode(n);
}
