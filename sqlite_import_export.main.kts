#!/usr/bin/env kotlin
@file:DependsOn("gephi-toolkit-0.10.0-all.jar")

/*
this code snippet port  java to kotolin script.
original java code is this url.
https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/SQLiteImportExport.java
 */


import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.gephi.graph.api.GraphController;
import org.gephi.io.exporter.api.ExportController
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.database.drivers.SQLUtils;
import org.gephi.io.database.drivers.SQLiteDriver;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

//Init a project - and therefore a workspace
val pc = Lookup.getDefault().lookup(ProjectController::class.java);
pc.newProject();
val workspace = pc.getCurrentWorkspace();

//Get controllers and models
val importController = Lookup.getDefault().lookup(ImportController::class.java);
val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace);


/*
The sample source code retrieves a sqlite file(lesmiserables.sqlite3) from a jar resource. However, I could not figure out how to retrieve the file from the those resource using kotlin. Therefore, I gave up on retrieving the sqlite file from the resource, and retrieved the file from the current directory.
*/

val db = EdgeListDatabaseImpl();
val current_dir = System.getProperty("user.dir")
val hostname = current_dir + "/resource/lesmiserables.sqlite3/"
db.setHost(hostname);
db.setSQLDriver(SQLiteDriver());
db.setNodeQuery("SELECT nodes.id AS id, nodes.label AS label FROM nodes");
db.setEdgeQuery("SELECT edges.source AS source, edges.target AS target, edges.label AS label, edges.weight AS weight FROM edges");
val edgeListImporter = ImporterEdgeList();
val container = importController.importDatabase(db, edgeListImporter);
container.getLoader().setAllowAutoNode(false);      //Don't create missing nodes
container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);   //Force UNDIRECTED
val sqlite_url = SQLUtils.getUrl(db.getSQLDriver(), db.getHost(), db.getPort(), db.getDBName());
println("sqlite_url = $}sqlite_url}")

//Append imported data to GraphAPI
importController.process(container, DefaultProcessor(), workspace);

//See if graph is well imported
val graph = graphModel.getUndirectedGraph();
println("Nodes: " + graph.getNodeCount());
println("Edges: " + graph.getEdgeCount());
//Layout - 100 Yifan Hu passes
val layout = YifanHuLayout(null, StepDisplacement(1f));
layout.setGraphModel(graphModel);
layout.resetPropertiesValues();
layout.initAlgo();
for(i in 0..400){
    if(layout.canAlgo()){
        if(i%100==0){
            println("Count=${i}")
        }
        layout.goAlgo()
    }
}
layout.endAlgo();

val ec = Lookup.getDefault().lookup(ExportController::class.java)
ec.exportFile(File("sql_lesmiserables.pdf"))

val url = SQLUtils.getUrl(db.getSQLDriver(), db.getHost(), db.getPort(), db.getDBName());
val connection = db.getSQLDriver().getConnection(url, db.getUsername(), db.getPasswd());
 //Update
var count:Int = 0;
for (node in graph.getNodes().toArray()) {
   val id = node.getId().toString();
   val x = node.x()
   val y = node.y();
   val query = "UPDATE nodes SET x = '" + x + "', y = '" + y + "' WHERE nodes.id='" + id + "'";
   try {
        val s = connection.createStatement();
        count += s.executeUpdate(query);
        s.close();
    } catch (e:SQLException) {
        System.err.println("Failed to update line node id = " + id);
        e.printStackTrace();
    }
}
System.err.println("${count} rows were updated");

//Close connection
connection?.close();
