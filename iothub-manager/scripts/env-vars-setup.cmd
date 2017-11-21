:: Prepare the environment variables used by the application.

:: Some settings are used to connect to an external dependency, e.g. Azure IoT Hub and IoT Hub Manager API
:: Depending on which settings and which dependencies are needed, edit the list of variables

:: see: Shared access policies => key name => Connection string
SETX PCS_IOTHUB_CONNSTRING "..."

SETX PCS_CONFIG_WEBSERVICE_URL "..."
