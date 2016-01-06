@ECHO OFF
ECHO Usage: MIN_CALIBRATION_VALUE MAX_CALIBRATION_VALUE SERVER_HOST SERVER_PORT %NL%
set /P min=MIN_CALIBRATION_VALUE:
set /P max=MAX_CALIBRATION_VALUE:
set /P host=SERVER_HOST:
set /P port=SERVER_PORT:
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Ptester -Dexec.args="%min% %max% xvsm %host% %port%"
PAUSE