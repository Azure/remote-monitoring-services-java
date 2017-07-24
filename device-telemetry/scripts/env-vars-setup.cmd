:: Prepare the environment variables used by the application.

:: The port where this project's web service is listening
:: See https://github.com/Azure/azure-iot-pcs-team/wiki/Architecture-draft
SET PCS_DEVICE_TELEMETRY_WEBSERVICE_PORT = "9004"

:: Connection information for Azure DocumentDb
SET PCS_DEVICE_TELEMETRY_DOCDB_CONN_STRING="..."