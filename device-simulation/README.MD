
> This folder references the *scripts* required for starting simulation micro-service in docker. The code of this micro-service resides [here](https://github.com/Azure/remote-monitoring-services-java/tree/master/device-simulation). 

## Running the service locally

## Prerequisites

1. Install docker-compose.
2. This service depends on the following microservices.  Run those services from the instruction in their README's in the following order.
   1. [Storage Adapter Microservice](https://github.com/Azure/remote-monitoring-services-java/tree/master/storage-adapter)
   2. [Config Microservice](https://github.com/Azure/remote-monitoring-services-java/tree/master/config)
    
2.  In order to run the service, some environment variables need to be created
at least once. See specific instructions for IDE or command line setup below
for more information. 
    * `PCS_AAD_APPID` = { Azure service principal id }
    * `PCS_AAD_APPSECRET` = { Azure service principal secret }
    * `PCS_KEYVAULT_NAME` = { Name of Key Vault resource that stores settings and configuration }

4. Start the device-simulation service using docker compose:
   ```
   cd scripts (in the device-simulation folder)
   cd docker
   docker-compose up OR
   run.cmd (win) OR
   ./run (Linux / Mac OS)
   ```
1. Use an HTTP client such as [Postman][postman-url], to exercise the
   [RESTful API][wiki-createsim-url] to create a simulation.


[iothub-url]: https://azure.microsoft.com/services/iot-hub
[storageadapter-url]: https://github.com/Azure/remote-monitoring-services-dotnet/tree/master/storage-adapter
[iothubconnstring-url]: https://blogs.msdn.microsoft.com/iotdev/2017/05/09/understand-different-connection-strings-in-azure-iot-hub
[docker-compose-install-url]: https://docs.docker.com/compose/install
[windows-envvars-howto-url]: https://superuser.com/questions/949560/how-do-i-set-system-environment-variables-in-windows-10
[postman-url]: https://www.getpostman.com
[wiki-createsim-url]: https://github.com/Azure/device-simulation-dotnet/wiki/%5BAPI-Specifications%5D-Simulations#create-default-simulation
[key-vault-url]: https://docs.microsoft.com/en-us/azure/azure-stack/user/azure-stack-key-vault-manage-portal
[pcs-cli-url]: https://github.com/Azure/pcs-cli
