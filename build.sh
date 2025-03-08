#!/bin/bash

# Compile the Java code
javac -d bin src/com/zadvornyi/aquarium/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    
    # Run the Java program
    java -cp bin com.zadvornyi.aquarium.Aquarium
else
    echo "Compilation failed!"
fi