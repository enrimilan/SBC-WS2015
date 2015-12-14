@ECHO OFF
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Pcalibrator -Dexec.args="xvsm"
PAUSE