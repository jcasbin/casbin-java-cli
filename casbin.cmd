@echo off
setlocal
set JAR_PATH=target\casbin-java-cli.jar
java -jar "%JAR_PATH%" %*
endlocal