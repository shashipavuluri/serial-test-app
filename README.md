# serial-test-app

This is a sample Application to read data from the serial port on gateway.

## Dependencies
This sample application uses serial communication ibraries from this location.
http://rxtx.qbang.org/wiki/index.php/Download

It uses the latest version 
rxtx 2.2pre2 (prerelease)       rxtx-2.2pre2-bins.zip   rxtx-2.2pre2.zip

rxt2.2 directory has these libs downloaded for convinience.

## Building the application jar
This application uses gradle to build the jar file. The following command can be used to build the jar file.
```
./gradlew clean build
```  
## Building package.tar.gz
Copy the jar file from build/libs to IOXPackage directory. This directory has all the files needed to build a package.

Contents of the IOxPackage directory:

package.yaml: (which mentions the serial device need to be enabled and launch.sh which mentions how to launch the application)

libs directory: which has the dependencies (the serial port communication library *.so) needed by the application. This directory will be part of java native library path definition when launching the application.

launch.sh: It mentions how to start the java application.

In order to build the iox package, "cd" into this directory and run the following:

```
ioxclient package .
```  

An ioxclient is also provided here in iox-client directory.

## Sample Code 
The code which show the usage of reading data from the serial port is located here.
```  
src/main/java/com/hbr/app/TestSerial.java   (Object which connects and reads data)
src/main/java/com/hbr/app/TestSerialApp.java (application)
```  


## Important note
Currently the sample application logs everything to System.out.println (to console).
Currently there is an issue in IoX where after sometime the application hangs if you log too much into console (there is an issue flusing the buffers).
The best solution is to log into a file. The sample applications in SDK show how to log to a file.

