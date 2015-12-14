@ECHO OFF
ECHO.
set /P usageArg= Usage: PART_TYPE AMOUNT
ECHO.
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Psupplier -Dexec.args="rmi %usageArg%"
PAUSE