set arg1=%1
set arg2=%2
call mvn -f  .\pom.xml exec:java -Ptester -Dexec.args="%arg1% %arg2% rmi"