@echo off
setlocal
set MAVEN_PROJECTBASEDIR=%CD%
set MAVEN_BINARY=.mvn\wrapper\maven-wrapper.jar

java -jar "%MAVEN_PROJECTBASEDIR%\%MAVEN_BINARY%" %*