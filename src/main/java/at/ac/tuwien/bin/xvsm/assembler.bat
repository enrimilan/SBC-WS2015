@ECHO OFF
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Passembler -Dexec.args="xvsm"
PAUSE