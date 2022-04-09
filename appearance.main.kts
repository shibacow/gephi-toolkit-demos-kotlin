#!/usr/bin/env kotlin

@file:DependsOn("gephi-toolkit-0.9.3-all.jar")

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
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.api.AppearanceModel

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

val appearanceController = Lookup.getDefault().lookup(AppearanceController::class.java)
val appearanceModel = appearanceController.getModel();
val degreeRanking = appearanceModel.getNodeFunction(graphModel.defaultColumns().degree(), 
RankingElementColorTransformer::class.java)
val degreeTransformer: RankingElementColorTransformer = degreeRanking.getTransformer()
val colors = listOf(Color(0xFEF0D9),Color(0xB30000))
degreeTransformer.setColors(colors.toTypedArray())
degreeTransformer.setColorPositions(arrayOf(0f,1f).toFloatArray())
appearanceController.transform(degreeRanking)
val centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS)
val centralityRanking = appearanceModel.getNodeFunction(centralityColumn, RankingNodeSizeTransformer::class.java)

/*
        //Rank size by centrality
        Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Function centralityRanking = appearanceModel.getNodeFunction(centralityColumn, RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer centralityTransformer = (RankingNodeSizeTransformer) centralityRanking.getTransformer();
        centralityTransformer.setMinSize(3);
        centralityTransformer.setMaxSize(10);
        appearanceController.transform(centralityRanking);

 */


