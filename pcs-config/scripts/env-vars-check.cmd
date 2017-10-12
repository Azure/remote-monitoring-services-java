@ECHO off & setlocal enableextensions enabledelayedexpansion

IF "%PCS_STORAGEADAPTER_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_STORAGEADAPTER_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

IF "%PCS_DEVICESIMULATION_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_DEVICESIMULATION_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

IF "%PCS_TELEMETRY_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_TELEMETRY_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

IF "%PCS_IOTHUBMANAGER_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_IOTHUBMANAGER_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

IF "%PCS_BINGMAP_KEY%" == "" (
    echo Error: the PCS_BINGMAP_KEY environment variable is not defined.
    exit /B 1
)

endlocal