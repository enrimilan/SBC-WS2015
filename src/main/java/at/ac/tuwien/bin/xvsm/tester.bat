@ECHO OFF
ECHO.
    set /P usageArg= Usage: MIN_CALIBRATION_VALUE MAX_CALIBRATION_VALUE
ECHO.
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Ptester -Dexec.args="xvsm %usageArg% rmi"
PAUSE