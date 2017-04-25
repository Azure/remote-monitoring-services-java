@ECHO off
setlocal

:: strlen("\scripts\") => 9
SET APP_HOME=%~dp0
SET APP_HOME=%APP_HOME:~0,-9%

cd %APP_HOME%

call gradlew distZip
IF NOT ERRORLEVEL 0 GOTO FAIL

echo Package available at:
dir build\distributions\*.zip

:: - - - - - - - - - - - - - -
goto :END

:FAIL
echo Command failed
endlocal
exit /B 1

:END
endlocal
