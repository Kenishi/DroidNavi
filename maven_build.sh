#!/bin/bash

# Change to Java gateway server and execute maven build
cd ./server/PCTeleLog
mvn clean install
rc=$?

if [[ $rc != 0 ]] ; then
    echo "Error building PyTeleLog Gateway Server"
    return $rc
fi

# Change to Python UI and execute maven build
cd ../pyqt-ui/PCTelelog-PyQt
mvn clean dependency:copy-dependencies assembly:assembly

if [[ $rc != 0 ]] ; then
	echo "Error building PyTeleLog UI"
	return $rc
fi

## Move to ./bin  ##

# Check bin exists
if [ -d ./bin ] ; then
	# Clean
	rm -R ./bin/*.*
else 
	mkdir ./bin
fi

# Copy
cp ./target/pctelelog-pyqt-*-distribution.zip ./bin/
