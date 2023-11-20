#!/bin/bash
./clean.sh

cd sharedobj
javac *.java
cd ..
cp ./sharedobj/*.class ./client/
cp ./sharedobj/*.class ./server/

cd client
javac ./*.java

cd ../server
javac ./*.java
