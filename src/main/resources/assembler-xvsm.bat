@ECHO OFF
set host=%1
set port=%2
call mvn -f  ..\..\..\..\..\..\..\..\pom.xml exec:java -Passembler -Dexec.args="xvsm %host% %port%"
EXIT