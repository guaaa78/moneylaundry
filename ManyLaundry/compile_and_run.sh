#!/bin/bash
# Many Laundry – Compile & Run Script
# Run this from the project root folder

mkdir -p out
echo "Compiling..."
javac -d out src/*.java && echo "Compile successful!" && java -cp out Main
