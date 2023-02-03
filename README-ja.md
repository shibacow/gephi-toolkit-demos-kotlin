# Kotolinを使ったGephi Toolkitデモ

Gephi Toolkitプロジェクトは、必要なモジュール（グラフ、レイアウト、フィルタ、IOなど）を標準的なJavaライブラリにパッケージしており、どのJavaプロジェクトでも物事を成し遂げるために使用することができます。

Javaを使用したGephi-Toolkitのデモは[こちらのサイト](https://github.com/gephi/gephi-toolkit-demos)にあります。

このプロジェクトは、上記javaのサイトを参考に、kotlinで書き直した。

# 上記Javaプロジェクトとの対応一覧

上記デモのjavaファイルは、以下のktsファイルに変換されました。

- HeadlessSimple.java -> headless_sample.main.kts
- WithAutoLayout.java -> auto_layout.main.kts
- ParallelWorkspace.java -> parallel_workspace.main.kts
- PartitionGraph.java -> partition_graph.main.kts 
- RankingGraph.java -> ranking_graph.main.kts
- Filtering.java -> filtering.main.kts
- ImportExport.java -> import_export.main.kts
- SQLiteImportExport.java -> sqlite_import_export.main.kts
- ManualGraph.java -> manual_graph.main.kts
- ManipulateAttributes.java -> manipulate_attributes.main.kts
- DynamicMetric.java -> dynamic_metric.main.kts
- ImportDynamic.java -> import_dynamic.main.kts

# デモの使用方法

kotlinのインストール

次のコマンドを実行 `kotlin headless_sample.main.kts`

## 簡単なデモ実行方法

githuに含まれるrun.shを実行する

```
./run.sh
```

## 注意

- ノード数が多いと、大量のメモリを消費する。
- アクセス権に注意する

上記のkotlinを実行時に、次の環境変数を設定した。

```
export MEM_SIZE=6
export JDK_JAVA_OPTIONS="-Xms${MEM_SIZE}g -Xmx${MEM_SIZE}g --add-opens=java.base/java.net=ALL-UNNAMED"
```
## 落とし穴

partition_graph.main.kts実行時、 `palette_default.csv` ,`palette_presets.csv` を読もうとするが、Javaのセキュリティ機能によりエラーになる。
そのため `JDK_JAVA_OPTIONS` に `--add-opens=java.base/java.net=ALL-UNNAMED` が必要になる

# 参照

See [website](https://gephi.org/toolkit/)
Source [code](https://github.com/gephi/gephi-toolkit)
