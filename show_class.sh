#!/bin/bash
# Set the JAR name
jar="gephi-toolkit-0.10.0-all.jar"
# Loop through the classes (everything ending in .class)
for class in $(jar -tf $jar | grep '.class'); do 
    # Replace /'s with .'s
    class=${class//\//.};
    # javap
    javap -classpath $jar ${class//.class/}; 
done