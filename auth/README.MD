[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

# Auth Microservice Overview

Auth micro-serivce is available only in dotnet. This folder contains the scripts required to run auth as a docker container locally. 

## Running the service locally

## Prerequisites

1. Install docker-compose.
1. In order to run the service, following environment variables are needed. These will be created and set in the inital deployment.
    * `PCS_AAD_APPID` = { Azure service principal id }
    * `PCS_AAD_APPSECRET` = { Azure service principal secret }
    * `PCS_KEYVAULT_NAME` = { Name of Key Vault resource that stores settings and configuration }

3. Configuration values used from Key Vault
Some of the configuration needed by the microservice is stored in an instance of Key Vault that was created on initial deployment. The auth microservice uses:

* `aadAppId` = Azure Active Directory application / service principal id.
* `aadAppSecret` = Azure Active Directory service princial secret.
* `aadEndpointUrl` = The AAD endpoint url to acquire ARM token for AAD application
* `authIssuer` = Identifies the security token service (STS) i.e. https://sts.windows.net/\{tenantId\}/
* `aadTenantId` = GUID representing your active directory tenant.
* `authRequired` = Whether or not authentication is needed for calls to microservices i.e. from the web ui or postman.
* `corsWhitelist ` = Specifies where requests are allowed from "{ 'origins': ['\*'], 'methods': ['\*'], 'headers': ['\*'] }" to allow everything. Empty to disable CORS.

4. Start the Auth microservice service using docker compose:
   ```
   cd scripts (in the auth folder)
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
[telemetry-microservice-url]: https://github.com/Azure/device-telemetry-dotnet
[configuration-microservice-url]: https://github.com/azure/pcs-config-dotnet
[postman-url]: https://www.getpostman.com
[dotnet-install]: https://www.microsoft.com/net/learn/get-started
[vs-install-url]: https://www.visualstudio.com/downloads
[dotnetcore-tools-url]: https://www.microsoft.com/net/core#windowsvs2017
[omnisharp-url]: https://github.com/OmniSharp/omnisharp-vscode
[windows-envvars-howto-url]: https://superuser.com/questions/949560/how-do-i-set-system-environment-variables-in-windows-10
[docker-compose-install-url]: https://docs.docker.com/compose/install
[rm-arch-url]:https://docs.microsoft.com/azure/iot-suite/iot-suite-remote-monitoring-sample-walkthrough
[run-with-docker-url]:(https://docs.microsoft.com/azure/iot-suite/iot-suite-remote-monitoring-deploy-local#run-the-microservices-in-docker)
