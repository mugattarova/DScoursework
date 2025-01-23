#!/bin/bash
./clean.sh

find ./src/ -type d | sed 's/\.\/src//g' | xargs -I {} mkdir -p bin"/{}"

cd src/sharedobj
javac -cp "..\lib\jgroups-3.6.20.Final.jar;." *.java 
cd ..
cp -R sharedobj/*.class client/
cp -R sharedobj/*.class server/

cd ../src/client
javac -cp "..\lib\jgroups-3.6.20.Final.jar;." *.java 

cd ../server
javac -cp "..\lib\jgroups-3.6.20.Final.jar;." *.java 

cd ../..
cp -R src/client/*.class bin/client/
cp -R src/client/*.der bin/client/
cp -R src/server/*.class bin/server/
cp -R src/server/*.der bin/server/
cp -R src/server/*.pem bin/server/

rm src/*/*.class
