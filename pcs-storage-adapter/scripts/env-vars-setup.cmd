:: Prepare the environment variables used by the application.

:: Some settings are used to connect to an external dependency, e.g. Azure IoT Hub and IoT Hub Manager API
:: Depending on which settings and which dependencies are needed, edit the list of variables

:: Connection information for Azure DocumentDb
SETX PCS_STORAGEADAPTER_CONTAINER_NAME "/dbs/.../colls/...

:: The DocumentDb connection string
SETX PCS_STORAGE_CONNSTRING "AccountEndpoint=https://....documents.azure.com:443/;AccountKey=...;"
