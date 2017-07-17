@ECHO off & setlocal enableextensions enabledelayedexpansion

IF "%PCS_DEVICE_TELEMETRY_WEBSERVICE_PORT%" == "" (
    echo Error: the PCS_DEVICE_TELEMETRY_WEBSERVICE_PORT environment variable is not defined.
    exit /B 1
)

IF "%PCS_DEVICE_TELEMETRY_DOCDB_CONN_STRING%" == "" (
    echo Error: the PCS_DEVICE_TELEMETRY_DOCDB_CONN_STRING environment variable is not defined.
    exit /B 1
)

endlocal
