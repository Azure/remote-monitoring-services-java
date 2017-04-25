@ECHO off
setlocal

:: strlen("\scripts\") => 9
SET APP_HOME=%~dp0
SET APP_HOME=%APP_HOME:~0,-9%

call %APP_HOME%\dotnet\scripts\build.cmd
IF NOT ERRORLEVEL 0 GOTO FAIL
call %APP_HOME%\java\scripts\build.cmd
IF NOT ERRORLEVEL 0 GOTO FAIL

:: - - - - - - - - - - - - - -
goto :END

:FAIL
echo Command failed
endlocal
exit /B 1

:END
endlocal
