@ECHO OFF
ECHO Usage: PART_TYPE AMOUNT SERVER_HOST:SERVER_PORT... %NL%
set /P partType=PART_TYPE:
set /P amount=AMOUNT:
set /P serverHost=SERVER_HOST:SERVER_PORT... :
mvn -f ..\..\..\..\..\..\..\..\pom.xml exec:java -Psupplier -Dexec.args="%partType% %amount% xvsm %serverHost%"
PAUSE