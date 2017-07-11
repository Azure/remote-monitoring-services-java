@ECHO off
setlocal

:: strlen("\scripts\") => 9
SET APP_HOME=%~dp0
SET APP_HOME=%APP_HOME:~0,-9%
cd %APP_HOME%

:: Check dependencies
java -version > NUL 2>&1
IF %ERRORLEVEL% NEQ 0 GOTO MISSING_JAVA

:: Package the application
call ./sbt assembly
IF %ERRORLEVEL% NEQ 0 GOTO FAIL

echo Package available at:
dir target/scala-2.11\*assembly*.jar

:: - - - - - - - - - - - - - -
goto :END

:MISSING_JAVA
    echo ERROR: 'java' command not found.
    echo Install OpenJDK or Oracle JDK and make sure the 'java' command is in the PATH.
    echo OpenJDK installation: http://openjdk.java.net/install
    echo Oracle Java Standard Edition: http://www.oracle.com/technetwork/java/javase/downloads
    exit /B 1

:FAIL
    echo Command failed
    endlocal
    exit /B 1

:END
endlocal
