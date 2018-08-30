@ECHO off & setlocal enableextensions enabledelayedexpansion

IF "%PCS_AUTH_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_AUTH_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

IF "%PCS_TELEMETRY_DOCUMENTDB_CONNSTRING%" == "" (
    echo Error: the PCS_TELEMETRY_DOCUMENTDB_CONNSTRING environment variable is not defined.
    exit /B 1
)

IF "%PCS_STORAGEADAPTER_WEBSERVICE_URL%" == "" (
    echo Error: the PCS_STORAGEADAPTER_WEBSERVICE_URL environment variable is not defined.
    exit /B 1
)

IF "%PCS_AUTH_ISSUER%" == "" (
    echo Error: the PCS_AUTH_ISSUER environment variable is not defined.
    exit /B 1
)

IF "%PCS_AUTH_AUDIENCE%" == "" (
    echo Error: the PCS_AUTH_AUDIENCE environment variable is not defined.
    exit /B 1
)

IF "%PCS_AAD_TENANT%" == "" (
    echo Error: the PCS_AAD_TENANT environment variable is not defined.
    exit /B 1
)

IF "%PCS_AAD_APPID%" == "" (
    echo Error: the PCS_AAD_APPID environment variable is not defined.
    exit /B 1
)

IF "%PCS_AAD_APPSECRET%" == "" (
    echo Error: the PCS_AAD_APPSECRET environment variable is not defined.
    exit /B 1
)

IF "%PCS_TELEMETRY_STORAGE_TYPE%" == "" (
    echo Error: the PCS_TELEMETRY_STORAGE_TYPE environment variable is not defined.
    exit /B 1
)

IF "%PCS_TSI_FQDN%" == "" (
    echo Error: the PCS_TSI_FQDN environment variable is not defined.
    exit /B 1
)

endlocal
