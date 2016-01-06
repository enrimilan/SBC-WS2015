@ECHO OFF
set partType=%1
set amount=%2
set serverHost=%3
call mvn -f  .\pom.xml exec:java -Psupplier -Dexec.args="%partType% %amount% rmi %serverHost%"
EXIT