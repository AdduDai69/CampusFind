@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "PATH=C:\tools\apache-maven-3.9.9\bin;%JAVA_HOME%\bin;%PATH%"
set "REPO=%~dp0.m2\repository"
"C:\tools\apache-maven-3.9.9\bin\mvn.cmd" "-Dmaven.repo.local=%REPO%" %*
