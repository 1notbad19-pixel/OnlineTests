@echo off
setlocal
set MAVEN_OPTS=-Xmx256m
java -jar "%~dp0\.mvn\wrapper\maven-wrapper.jar" %*