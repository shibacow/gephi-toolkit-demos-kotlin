#!/usr/bin/env kotlin

//@file:Repository("https://mvnrepository.com")
@file:DependsOn("org.json:json:20220320")

import org.json.*

val jsonData = """{
    "penList" : [
        {"name": "red-bold", "color": "240,0,0", "width": 6},
        {"name": "black-normal", "color": "0,0,0"},
        {"name": "light", "width": 3},
        {"color": "102,102,102"}
    ]
}"""

val rootObj = JSONObject(jsonData)
val penListArray =  rootObj.getJSONArray("penList")

0.until( penListArray.length() ).forEach { index->
    val penObject = penListArray[index] as JSONObject
    println( "- ${penObject}" )
}