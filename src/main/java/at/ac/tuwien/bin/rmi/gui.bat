@ECHO OFF
call mvn -f ..\..\..\..\..\..\..\..\pom.xml install
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Pdrone-rmi
PAUSE