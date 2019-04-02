[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

# Azure Stream Analytics Manager Overview

ASA-manager micro-serivce is available only in dotnet. This folder contains the scripts required to run asa-manager as a docker container locally.

## Running the service locally

## Prerequisites

1. Install docker-compose.
2. This service depends on the following repositories. Run those services from the instruction in their README's in the following order.

    1. [Storage Adapter Microservice](https://github.com/Azure/remote-monitoring-services-dotnet/tree/master/storage-adapter)
    1. [Telemetry Microservice][telemetry-microservice-url]
    1. [Iot Hub Manager Microservice](https://github.com/Azure/remote-monitoring-services-dotnet/tree/master/iothub-manager)
    1. [Configuration Microservice][configuration-microservice-url] 

2. In order to run the service, following environment variables are needed. These will be created and set in the inital deployment.

    * `PCS_AAD_APPID` = { Azure service principal id }
    * `PCS_AAD_APPSECRET` = { Azure service principal secret }
    * `PCS_KEYVAULT_NAME` = { Name of Key Vault resource that stores settings and configuration }.

3. Configuration values used from Key Vault
Some of the configuration needed by the microservice is stored in an instance of Key Vault that was created on initial deployment. The ASA-manager microservice uses:
    * `documentDBConnectionString` = { Azure Cosmos DB connection string }
    * `telemetryWebServiceUrl ` = http://localhost:9004/v1
    * `configWebServiceUrl ` = http://localhost:9005/v1
    * `iothubmanagerWebServiceUrl` = http://localhost:9002/v1
    * `storageAccountName` = { Azure Blob Storage account name }
    * `storageAccountKey` = { Azure Blob Storage Key }
    * `storageEndpointSuffix` = { Azure Blob Storage endpoint suffix, 
    ex. "core.windows.net" }
    * `messagesEventHubConnectionString` = { Event Hub Connection String for RootManageSharedAccessKey }
    * `messagesEventHubName` = { Name of Event Hub }
    * `telemetryStorageType` = { "tsi" or "cosmosdb", default is "tsi" }

4. Start the ASA manager service using docker compose:
   ```
   cd scripts (in the asa-manager folder)
   cd docker
   docker-compose up OR
   run.cmd (win) OR
   ./run (Linux / Mac OS)
   ```
# Feedback

Please enter issues, bugs, or suggestions as 
[GitHub Issues](https://github.com/Azure/asa-manager-dotnet/issues)

# License

Copyright (c) Microsoft Corporation. All rights reserved.
Licensed under the [MIT](LICENSE) License.

[build-badge]:https://solutionaccelerators.visualstudio.com/RemoteMonitoring/_apis/build/status/Consolidated%20Repo
[build-url]: https://solutionaccelerators.visualstudio.com/RemoteMonitoring/_build/latest?definitionId=22
[issues-badge]: https://img.shields.io/github/issues/azure/asa-manager-dotnet.svg
[issues-url]: https://github.com/azure/asa-manager-dotnet/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-solutions.js.svg
[gitter-url]: https://gitter.im/azure/iot-solutions


[asa-url]: https://azure.microsoft.com/services/stream-analytics
[telemetry-microservice-url]: https://github.com/Azure/remote-monitoring-services-dotnet/tree/master/device-telemetry
[configuration-microservice-url]: https://github.com/Azure/remote-monitoring-services-dotnet/tree/master/config
[postman-url]: https://www.getpostman.com
[dotnet-install]: https://www.microsoft.com/net/learn/get-started
[vs-install-url]: https://www.visualstudio.com/downloads
[dotnetcore-tools-url]: https://www.microsoft.com/net/core#windowsvs2017
[omnisharp-url]: https://github.com/OmniSharp/omnisharp-vscode
[windows-envvars-howto-url]: https://superuser.com/questions/949560/how-do-i-set-system-environment-variables-in-windows-10
[docker-compose-install-url]: https://docs.docker.com/compose/install
[rm-arch-url]:https://docs.microsoft.com/azure/iot-suite/iot-suite-remote-monitoring-sample-walkthrough
[run-with-docker-url]:(https://docs.microsoft.com/azure/iot-suite/iot-suite-remote-monitoring-deploy-local#run-the-microservices-in-docker)
