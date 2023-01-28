# kotlin_for_gephi

[![kotlin_gephi_test](https://github.com/shibacow/kotolin_for_gephi/actions/workflows/kotolin_gephi_test.yaml/badge.svg)](https://github.com/shibacow/kotolin_for_gephi/actions/workflows/kotolin_gephi_test.yaml)

kotlin で gephi libraryを使う。そのためのサンプルスクリプト。
kotlinでも動くことを確認するため、[Gephi Toolkit Demos](https://github.com/gephi/gephi-toolkit-demos) の 
- `HeadlessSimple.java` 
- `WithAutoLayout.java`
- `ParallelWorkspace.java`
- `PartitionGraph.java`
- `RankingGraph.java`
- `Filtering.java`
- `ImportExport.java`
- `SQLiteImportExport.java`
- `ManualGraph.java`
- `ManipulateAttributes.java`
- `DynamicMetric.java`
- `ImportDynamic.java`

を移植した。


# ハマったところ

## partition_graph,PaletteManager.java内部でリソースを読む際の権限エラー

partition_graph.main.kts [PaletteManager.getInstance()](https://github.com/gephi/gephi/blob/a1853b707a1a86c0594417bf4cb296da30967747/modules/AppearancePlugin/src/main/java/org/gephi/appearance/plugin/palette/PaletteManager.java) の中で、`palette_default.csv` , `palette_presets.csv` を読むが権限エラーになる(javaのセキュリティが強化されたため)。そのため、`JDK_JAVA_OPTIONS` に `--add-opens=java.base/java.net=ALL-UNNAMED` が必要になる。
