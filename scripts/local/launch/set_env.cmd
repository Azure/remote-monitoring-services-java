SET bat_file=".env.cmd"
SET envFile=".env"

cd scripts\local\launch

@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

for /f "tokens=*" %%a in ('Type %envFile%') do (
  Set line=%%a
  if "!line:~0,1!"=="#" (
    echo "-"
  ) else (
    Set !line!
  )
)
pause