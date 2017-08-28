:: Prepare the environment variables used by the application.

:: Some settings are used to connect to an external dependency, e.g. Azure IoT Hub and IoT Hub Manager API
:: Depending on which settings and which dependencies are needed, edit the list of variables


:: The DocumentDb connection string
SETX PCS_STORAGEADAPTER_DOCUMENTDB_CONNSTRING "AccountEndpoint=https://....documents.azure.com:443/;AccountKey=...;"
