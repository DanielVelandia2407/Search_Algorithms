@echo off
echo Building JAR file...

REM Create the JAR file from the compiled classes
cd %~dp0
jar cfm SearchAlgorithms.jar MANIFEST.MF -C out\production\search_algorithms .

echo JAR file created: SearchAlgorithms.jar
echo You can run it with: java -jar SearchAlgorithms.jar