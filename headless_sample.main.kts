#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/HeadlessSimple.java
 */

import org.gephi.project.api.ProjectController
import org.gephi.io.importer.api.ImportController
import org.gephi.io.exporter.api.ExportController
import org.gephi.filters.api.FilterController
import org.gephi.preview.api.PreviewController
import org.gephi.datalab.api.AttributeColumnsController
import org.gephi.io.importer.api.EdgeDirectionDefault
import org.gephi.graph.api.GraphController
import org.gephi.io.processor.plugin.DefaultProcessor
import org.openide.util.Lookup
import java.io.File
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter
import org.gephi.filters.api.Range
import org.gephi.filters.api.Query
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout
import org.gephi.layout.plugin.force.StepDisplacement
import org.gephi.statistics.plugin.GraphDistance
import org.gephi.preview.api.PreviewProperty
import org.gephi.preview.types.EdgeColor
import java.awt.Color
import org.gephi.appearance.api.AppearanceController
import org.gephi.appearance.plugin.RankingElementColorTransformer
import org.gephi.appearance.plugin.RankingNodeSizeTransformer
import org.gephi.appearance.api.AppearanceModel

//Init a project - and therefore a workspace

val pc = Lookup.getDefault().lookup(ProjectController::class.java)
pc.newProject()
val workspace = pc.getCurrentWorkspace();

//Import file
val imc = Lookup.getDefault().lookup(ImportController::class.java)
val file = File("polblogs.gml")
val container = imc.importFile(file)

//Append imported data to GraphAPI
container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED)
imc.process(container,DefaultProcessor(),workspace)

//See if graph is well imported
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel()
val graph = graphModel.getDirectedGraph()
println("Nodes: " + graph.getNodeCount())
println("Edges: " + graph.getEdgeCount())

//Filter
val degreeFilter = DegreeRangeFilter()
degreeFilter.init(graph)
degreeFilter.setRange(Range(30, Int.MAX_VALUE))
val filterController = Lookup.getDefault().lookup(FilterController::class.java)
val query = filterController.createQuery(degreeFilter)
val view = filterController.filter(query)
graphModel.setVisibleView(view)

//See visible graph stats
val graphVisible = graphModel.getUndirectedGraphVisible();
println("Undirected Nodes: " + graphVisible.getNodeCount());
println("Undirected Edges: " + graphVisible.getEdgeCount());

//Run YifanHuLayout for 4000 passes - The layout always takes the current visible view
val layout = YifanHuLayout(null, StepDisplacement(1f));
layout.setGraphModel(graphModel);
layout.resetPropertiesValues();
layout.setOptimalDistance(200f);
layout.initAlgo();
 
for(i in 0..4000){
    if(layout.canAlgo()){
        if(i%100==0){
            println("Count="+i)
        }
        layout.goAlgo()
    }
}

layout.endAlgo()

//Get Centrality
val distance = GraphDistance()
distance.setDirected(true)
distance.execute(graphModel)

//Rank color by Degree
val appearanceController = Lookup.getDefault().lookup(AppearanceController::class.java)
val appearanceModel = appearanceController.getModel();
val degreeRanking = appearanceModel.getNodeFunction(graphModel.defaultColumns().degree(), 
RankingElementColorTransformer::class.java)
val degreeTransformer: RankingElementColorTransformer = degreeRanking.getTransformer()
val colors = listOf(Color(0xFEF0D9),Color(0xB30000))
degreeTransformer.setColors(colors.toTypedArray())
degreeTransformer.setColorPositions(arrayOf(0f,1f).toFloatArray())
appearanceController.transform(degreeRanking)

//Rank size by centrality
val centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS)
val centralityRanking = appearanceModel.getNodeFunction(centralityColumn, RankingNodeSizeTransformer::class.java)
val centralityTransformer: RankingNodeSizeTransformer = centralityRanking.getTransformer()
centralityTransformer.setMinSize(3f)
centralityTransformer.setMaxSize(10f)
appearanceController.transform(centralityRanking)

//Preview
val model = Lookup.getDefault().lookup(PreviewController::class.java).getModel()

model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, true)
model.getProperties().putValue(PreviewProperty.EDGE_COLOR, EdgeColor(Color.GRAY))
model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 0.1f);

model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));

//Export
val ec = Lookup.getDefault().lookup(ExportController::class.java)
ec.exportFile(File("headless_simple.pdf"))
