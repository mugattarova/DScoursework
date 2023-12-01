#!/bin/bash
./clean.sh

cd sharedobj
javac -cp "./lib/jgroups-3.6.20.Final.jar;." *.java
cd ..
cp ./sharedobj/*.class ./client/
cp ./sharedobj/*.class ./server/

cd client
javac -cp "./lib/jgroups-3.6.20.Final.jar;." *.java

cd ../server
javac -cp "./lib/jgroups-3.6.20.Final.jar;." *.java
