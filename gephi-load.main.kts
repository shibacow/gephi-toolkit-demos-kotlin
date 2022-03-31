#!/usr/bin/env kotlin

@file:DependsOn("gephi-toolkit-0.9.2-all.jar")

import org.gephi.project.api.ProjectController
import org.gephi.io.importer.api.ImportController
import org.gephi.io.exporter.api.ExportController
import org.gephi.filters.api.FilterController
import org.gephi.io.importer.api.EdgeDirectionDefault
import org.gephi.graph.api.GraphController
import org.gephi.io.processor.plugin.DefaultProcessor
import org.openide.util.Lookup
import java.io.File
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter
import org.gephi.filters.api.Range
import org.gephi.filters.api.Query
//import org.gephi.project.api.Workspace;
//import org.gephi.io.importer.api.Container;
//import org.gephi.io.generator.plugin.RandomGraph;
//import org.gephi.io.importer.api.ImportController;
//import org.gephi.io.processor.plugin.DefaultProcessor;
//import org.gephi.io.processor.plugin.AppendProcessor;
//import org.gephi.io.generator.plugin.DynamicGraph;

val pc = Lookup.getDefault().lookup(ProjectController::class.java)
pc.newProject()
val workspace = pc.getCurrentWorkspace();
val imc = Lookup.getDefault().lookup(ImportController::class.java)
val file = File("polblogs.gml")
val container = imc.importFile(file)
container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED)
imc.process(container,DefaultProcessor(),workspace)
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel()
val graph = graphModel.getDirectedGraph()
println("Nodes: " + graph.getNodeCount())
println("Edges: " + graph.getEdgeCount())
val degreeFilter = DegreeRangeFilter()
degreeFilter.init(graph)
degreeFilter.setRange(Range(30, Int.MAX_VALUE))
val filterController = Lookup.getDefault().lookup(FilterController::class.java)
/* 
val query = filterController.createQuery(degreeFilter)
val view = filterController.filter(query)
graphModel.setVisibleView(view)
*/


val ec = Lookup.getDefault().lookup(ExportController::class.java)
ec.exportFile(File("headless_simple.pdf"))