#!/bin/bash
cd keygen

javac *.java
java KeyGeneration

cd ..
cp ./keygen/publickeyServer.txt ./client/
cp ./keygen/*.txt ./server/

cd keygen
rm *.class
