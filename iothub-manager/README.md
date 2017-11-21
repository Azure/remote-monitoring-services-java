
[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

Iot Hub Manager Overview 
==========================
This service handles communication with the Azure Iot Hub for devices such as 
registration, queries, jobs, properties, and tags. 

The microservice provides a RESTful endpoint to query for devices registered 
to the Iot Hub, and to register devices to the IoT Hub in order to manage jobs,
properties, and tags. 

Dependencies
============
- Instance of Azure Iot Hub: https://azure.microsoft.com/services/iot-hub

How to use the microservice
===========================
## Quickstart - Running the service with Docker

1. Make sure you have installed Docker and have an Iot Hub instance. 
   (See [Dependencies](#dependencies) section)
1. Find your Iot Hub connection string. 
   [Help finding Iot Hub Connection String](https://blogs.msdn.microsoft.com/iotdev/2017/05/09/understand-different-connection-strings-in-azure-iot-hub/)
1. Store the "IoT Hub Connection string" in the 
   [env-vars-setup](scripts)
   script, then run the script. (see 
   [Configuration and Environment variables](#configuration-and-environment-variables)
   for more info)
1. Run the service using the [docker-compose script](scripts):
	```
	cd scripts/docker
	docker-compose up
	```
1. Use an HTTP client such as [Postman](https://www.getpostman.com),
   to exercise the 
   [RESTful API](https://github.com/Azure/iothub-manager-java/wiki/%5BAPI-Specifications%5D-Devices) to list devices.
	```
	GET /v1/devices
	```

Running the service in an IDE
=============================
## Prerequisites
- Install Intellij IDEA Community: https://www.jetbrains.com/idea/download
- Install SBT: http://www.scala-sbt.org/download.html
- Install the latest Java SDK: 
  http://www.oracle.com/technetwork/java/javase/downloads/index.html

## Running the service with IntelliJ IDEA
1. In IntelliJ, New > Project from existing sources
1. in 'Import project from external model' select the SBT icon
1. Select "Use auto-import"
1. When the solution is loaded, got to `Run -> Edit Configurations` and give it
   a name
1. Under 'Tasks' type `"run 9002"` with the quotes
1. Add a new environment variable with name
   `PCS_IOTHUB_CONNSTRING` storing your Azure IoT Hub connection string and `PCS_CONFIG_WEBSERVICE_URL` for the URL of the config service.
   [Help finding Iot Hub Connection String](https://blogs.msdn.microsoft.com/iotdev/2017/05/09/understand-different-connection-strings-in-azure-iot-hub/)
1. Save the settings and run the configuration just created, from the IDE
   toolbar.
1. You should see the service bootstrap messages in IntelliJ Run window,
   with details such as the URL where the web service is running, plus
   the service logs.
1. Use an HTTP client such as [Postman](https://www.getpostman.com),
   to exercise the 
   [RESTful API](https://github.com/Azure/iothub-manager-java/wiki/%5BAPI-Specifications%5D-Devices) to list devices.
   ```
   GET /v1/devices
   ```

Project Structure
=================
This microservice contains the following projects:
* **app/com/microsoft/azure/iotsolutions/iothubmanager**
    * **WebService** - web service exposing REST interface for Iot Hub
    communication.
    * **Services** - business logic for interacting with IoTHub
* **test** 
    * **runtime** - Unit tests for configuration
* **conf** - configuration files and routes
* **scripts** - contains build scripts, docker container creation scripts, 
   and scripts for running the microservice from the command line

Build and Run from the command line
===================================
The [scripts](scripts) folder contains scripts for many frequent tasks:

* `build`: compile all the projects and run the tests.
* `compile`: compile all the projects.
* `run`: compile the projects and run the service. This will prompt for
  elevated privileges in Windows to run the web service.

Updating the Docker image
=========================

The `scripts` folder includes a [docker](scripts/docker) subfolder with the 
files required to package the service into a Docker image:

* `Dockerfile`: docker images specifications
* `build`: build a Docker container and store the image in the local registry
* `run`: run the Docker container from the image stored in the local registry
* `content`: a folder with files copied into the image, including the entry
   point script

You can also start Iot Hub Manager and its dependencies in one simple step,
using Docker Compose with the
[docker-compose.yml](scripts/docker/docker-compose.yml) file in the project:

```
cd scripts/docker
docker-compose up
```

The Docker compose configuration requires the IoT Hub environment variable.
(see [Configuration and Environment variables](#configuration-and-environment-variables))

Configuration and Environment variables
=======================================
## Configuration
The service configuration is stored in
[`conf/application.conf`](conf/application.conf). The format allows to store 
values in a readable format, with comments.
The application also supports inserting environment variables, such as
credentials and networking details.

The configuration file in the repository references the Iot Hub environment
variable that is required to be created at least once. Depending on your OS and
the IDE, there are several ways to manage environment variables:

## Environment Variable
**REQUIRED** - `PCS_IOTHUB_CONNSTRING={your Iot Hub Connection String}` 
(see [Help finding Iot Hub Connection String](https://blogs.msdn.microsoft.com/iotdev/2017/05/09/understand-different-connection-strings-in-azure-iot-hub/) for more info)

**REQUIRED** - `PCS_CONFIG_WEBSERVICE_URL={your Config service URL}`

* For Windows users, the [env-vars-setup.cmd](scripts/env-vars-setup.cmd)
  script needs to be prepared and executed just once. When executed, the
  settings will persist across terminal sessions and reboots.
* For Linux and OSX environments, the [env-vars-setup](scripts/env-vars-setup)
  script needs to be executed every time a new console is opened.
  Depending on the OS and terminal, there are ways to persist values
  globally, for more information these pages should help:
  * https://stackoverflow.com/questions/13046624/how-to-permanently-export-a-variable-in-linux
  * https://stackoverflow.com/questions/135688/setting-environment-variables-in-os-x
  * https://help.ubuntu.com/community/EnvironmentVariables
* IntelliJ IDEA: env. vars can be set in each Run Configuration
  https://www.jetbrains.com/help/idea/run-debug-configuration-application.html 

Contributing to the solution
============================
Please follow our [contribution guildelines](CONTRIBUTING.md) and code style 
conventions.

Feedback
========
Please enter issues, bugs, or suggestions as GitHub Issues here:
https://github.com/Azure/iot-hub-manager-java/issues.

[build-badge]: https://img.shields.io/travis/Azure/iothub-manager-java.svg
[build-url]: https://travis-ci.org/Azure/iothub-manager-java
[issues-badge]: https://img.shields.io/github/issues/azure/iothub-manager-java.svg
[issues-url]: https://github.com/azure/iothub-manager-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-solutions.js.svg
[gitter-url]: https://gitter.im/azure/iot-solutions
