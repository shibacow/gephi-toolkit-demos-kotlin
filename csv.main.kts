#!/usr/bin/env kotlin

//@file:Repository("https://mvnrepository.com")
@file:DependsOn("com.opencsv:opencsv:5.5")

import java.io.*
import com.opencsv.CSVReader
import com.opencsv.CSVWriter

val inputCsvFile = File("price.csv")
val outputCsvFile = File("price-result.csv")

val csvReader = CSVReader(InputStreamReader(FileInputStream(inputCsvFile), "UTF-8"))
val rows: List<Array<String>> = csvReader.readAll()
csvReader.close()

rows.forEach { row->
    println( row.joinToString("|") )
}

val csvWriter = CSVWriter(OutputStreamWriter(FileOutputStream(outputCsvFile),"UTF-8"))
csvWriter.writeNext( rows[0] )
IntRange(1, rows.size-1).forEach { index->
    val row = rows[index]
    csvWriter.writeNext( arrayOf<String>(row[0], row[1], (row[2].toInt() * 2).toString() ) )
}

csvWriter.close()