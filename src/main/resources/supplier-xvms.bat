set arg1=%1
set arg2=%2
call mvn -f  .\pom.xml exec:java -Psupplier -Dexec.args="xvsm %arg1% %arg2%"