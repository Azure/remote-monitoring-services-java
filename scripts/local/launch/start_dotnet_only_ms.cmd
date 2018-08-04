set BAT_DIR=%~dp0
cd %BAT_DIR%
cd ..\..\..\
set APP_HOME=%cd%

call %APP_HOME%\scripts\local\launch\set_env.cmd
call %APP_HOME%\scripts\local\launch\.env_uris.cmd

cd %APP_HOME%\device-simulation\scripts\docker
START "/B" run.cmd
cd %APP_HOME%\asa-manager\scripts\docker
START "/B" run.cmd
cd %APP_HOME%\pcs-auth\scripts\docker
START "/B" run.cmd