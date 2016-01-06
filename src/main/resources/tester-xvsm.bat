@ECHO OFF
set min=%1
set max=%2
set host=%3
set port=%4
call mvn -f  ..\..\..\..\..\..\..\..\pom.xml exec:java -Ptester -Dexec.args="%min% %max% xvsm %host% %port%"
EXIT