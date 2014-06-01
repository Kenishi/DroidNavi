#!/bin/bash

# Change to Java gateway server and execute maven build
cd ./server
mvn clean install
rc=$?

if [[ $rc != 0 ]] ; then
    echo "Error building PyTeleLog Gateway Server"
    return $rc
fi

# Change to Python UI and execute maven build
cd ../pyqt-ui
mvn clean dependency:copy-dependencies package

if [[ $rc != 0 ]] ; then
	echo "Error building PyTeleLog UI"
	return $rc
fi

## Move to ./bin  ##
# Back to root
cd ../

# Check bin exists
if [ -d ./bin ] ; then
	# Clean
	rm -R ./bin/*.*
else 
	mkdir ./bin
fi

# Copy
cp ./pyqt-ui/target/pctelelog-pyqt-*-distribution.zip ./bin/
