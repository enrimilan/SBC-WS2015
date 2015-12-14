@ECHO OFF
set arg1=%1
ECHO.
set /P usageArg= Usage: PART_TYPE AMOUNT
ECHO.
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Psupplier -Dexec.args="xvsm %usageArg%"
PAUSE