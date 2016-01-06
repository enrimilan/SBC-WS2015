@ECHO OFF
set host=%1
set port=%2
call mvn -f  .\pom.xml exec:java -Pcalibrator -Dexec.args="rmi %host% %port%"
EXIT