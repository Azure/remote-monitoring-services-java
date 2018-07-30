@ECHO off & setlocal enableextensions enabledelayedexpansion

IF "%PCS_TELEMETRY_DOCUMENTDB_CONNSTRING%" == "" (
    echo Error: the PCS_TELEMETRY_DOCUMENTDB_CONNSTRING environment variable is not defined.
    exit /B 1
)

IF "%PCS_STORAGEADAPTER_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_STORAGEADAPTER_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

endlocal
