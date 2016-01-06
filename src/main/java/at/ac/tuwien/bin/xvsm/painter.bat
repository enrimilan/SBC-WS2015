@ECHO OFF
ECHO Usage: SERVER_HOST SERVER_PORT %NL%
set /P host=SERVER_HOST:
set /P port=SERVER_PORT:
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Ppainter -Dexec.args="xvsm %host% %port%"
PAUSE