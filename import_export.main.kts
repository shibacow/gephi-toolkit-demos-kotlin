#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.1-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/ImportExport.java
 */


import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperty;
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
// [differs from Java original] Java loads lesmiserables.gml from classpath via getClass().getResource().
// Here we load polblogs.gml from a local path instead.
val imc = Lookup.getDefault().lookup(ImportController::class.java)
var filename="resource/polblogs.gml"
//filename="resource/example.gml"
try{
    val file = File(filename)
    val container = imc.importFile(file)
    container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED)
    container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
    imc.process(container,DefaultProcessor(),workspace)
}catch(ex: IOException){
    ex.printStackTrace()
}

val ec = Lookup.getDefault().lookup(ExportController::class.java);

try {
  ec.exportFile(File("io_gexf.gexf"));
} catch (ex: IOException) {
   ex.printStackTrace();
}

val exporter: GraphExporter = ec.getExporter("gexf") as GraphExporter;     //Get GEXF exporter
exporter.setExportVisible(true);  //Only exports the visible (filtered) graph
exporter.setWorkspace(workspace);
try {
   // [differs from Java original] Java exports to "io_gexf.gexf" (same name as the full export above),
   // overwriting it. Here we use a distinct name to keep both files.
   ec.exportFile(File("io_gexf_visible.gexf"), exporter);
} catch (ex: IOException) {
   ex.printStackTrace();
}

//Export to Writer
val exporterGraphML = ec.getExporter("graphml");     //Get GraphML exporter
exporterGraphML.setWorkspace(workspace);
val stringWriter = StringWriter();
ec.exportWriter(stringWriter,  exporterGraphML as CharacterExporter);
//println(stringWriter.toString());   //Uncomment this line


// [differs from Java original] Java has no preview configuration here.
// gephi-toolkit 0.10.1 upgraded PDFBox to 3.x, which throws IllegalArgumentException on NaN
// coordinates. polblogs.gml contains many reciprocal edge pairs (A→B and B→A); Gephi renders
// those as curved edges, and ArrowRenderer's arc-arrow calculation produces NaN for certain
// edge configurations. Setting EDGE_CURVED=false avoids the curved-edge code path entirely.
val previewController = Lookup.getDefault().lookup(PreviewController::class.java)
previewController.getModel(workspace).getProperties().putValue(PreviewProperty.EDGE_CURVED, false)

//PDF Exporter config and export to Byte array
val pdfExporter = ec.getExporter("pdf") as PDFExporter;
pdfExporter.setPageSize(PDRectangle.A0);
pdfExporter.setWorkspace(workspace);
val baos = ByteArrayOutputStream();
ec.exportStream(baos, pdfExporter);
val pdf = baos.toByteArray();
try{
   // [differs from Java original] Java only holds the PDF as a byte array and does not write it to disk.
   // Here we persist it to a file so the output can be inspected.
   File("import_export_demo_byte_a0.pdf").writeBytes(pdf)
} catch (ex: IOException) {
   ex.printStackTrace();
}
