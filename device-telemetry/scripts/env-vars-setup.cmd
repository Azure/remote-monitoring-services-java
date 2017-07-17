:: Prepare the environment variables used by the application.

:: The port where this project's web service is listening
:: See https://github.com/Azure/azure-iot-pcs-team/wiki/Architecture-draft
SET PCS_DEVICE_TELEMETRY_WEBSERVICE_PORT = "9004"

:: Some settings are used to connect to an external dependency, e.g. Azure IoT Hub and IoT Hub Manager API
:: Depending on which settings and which dependencies are needed, edit the list of variables

:: see: Shared access policies => key name => Connection string
SET PCS_DEVICE_TELEMETRY_DOCDB_CONN_STRING = "..."
