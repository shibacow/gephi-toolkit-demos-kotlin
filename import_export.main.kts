#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

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


//PDF Exporter config and export to Byte array
val pdfExporter = ec.getExporter("pdf") as PDFExporter;
pdfExporter.setPageSize(PDRectangle.A0);
pdfExporter.setWorkspace(workspace);
val baos = ByteArrayOutputStream();
ec.exportStream(baos, pdfExporter);
val pdf = baos.toByteArray();
try{
 File("import_export_demo_byte_a0.pdf").writeBytes(pdf)
} catch (ex: IOException) {
   ex.printStackTrace();
}