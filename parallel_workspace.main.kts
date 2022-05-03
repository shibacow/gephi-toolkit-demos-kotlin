#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.9.3-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/ParallelWorkspace.java
 */

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

var pc = Lookup.getDefault().lookup(ProjectController::class.java)
pc.newProject()
var workspace1 = pc.getCurrentWorkspace()

//Generate a new random graph into a container
val container = Lookup.getDefault().lookup(Container.Factory::class.java).newContainer()
val randomGraph = RandomGraph()
randomGraph.setNumberOfNodes(500);
randomGraph.setWiringProbability(0.005);
randomGraph.generate(container.getLoader());

//Append container to graph structure
val importController = Lookup.getDefault().lookup(ImportController::class.java);
importController.process(container, DefaultProcessor(), workspace1)

val workspace2 = pc.duplicateWorkspace(workspace1)

val executor = Executors.newFixedThreadPool(2)

val f1 = executor.submit(createLayoutRunnable(workspace1))
val f2 = executor.submit(createLayoutRunnable(workspace2))

try {
    f1.get()
    f2.get()
} catch (Exception ex) {
    Exceptions.printStackTrace(ex)
}
executor.shutdown()

fun createLayoutRunnable(workspace) {
        return Runnable() {
            fun run() {
                val gm = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace);
                val autoLayout = AutoLayout(10, TimeUnit.SECONDS)
                autoLayout.setGraphModel(gm);
                autoLayout.addLayout(YifanHuLayout(null, StepDisplacement(1f)), 1f);
                autoLayout.execute();
            }
        };
    }




