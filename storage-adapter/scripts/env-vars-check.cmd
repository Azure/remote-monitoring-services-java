@ECHO off & setlocal enableextensions enabledelayedexpansion

:: Some settings are used to connect to an external dependency, e.g. Azure IoT Hub and IoT Hub Manager API
:: Depending on which settings and which dependencies are needed, edit the list of variables checked

IF "%PCS_STORAGEADAPTER_DOCUMENTDB_CONNSTRING%" == "" (
    echo Error: the PCS_STORAGEADAPTER_DOCUMENTDB_CONNSTRING environment variable is not defined.
    exit /B 1
)


endlocal
