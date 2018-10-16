
[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

# Config Service Overview
This service handles communication with the [Storage Adapter] microservice to complete tasks.

The microservice provides a RESTful endpoint to make CRUD operation for
"devicegroups","solution-settings" and "user-settings".
The data will be stored by the [Storage Adapter] microservice.

## Why?

This microservice was built as part of the
[Azure IoT Remote Monitoring](https://github.com/Azure/azure-iot-pcs-remote-monitoring-java)
project to provide a generic implementation for an end-to-end IoT solution.
More information [here][rm-arch-url].

## Features
* Create or update device groups
* Get all or a single device group
* Get or upload logo
* Get or set overall solution settings
* Get or set individual user settings
* Create or delete a package
* Get all or a single uploaded package.

## Documentation
* View the API documentation in the [Wiki](https://github.com/Azure/pcs-config-java/wiki).

# How to Use

## Running the service with Docker

You can run the microservice and its dependencies using
[Docker](https://www.docker.com/) with this instructions [here][run-with-docker-url].

## Running the service locally

## Prerequisites

### 1. Deploy Azure Services

This service has a dependency on the following Azure resources.
Follow the instructions for
[Deploy the Azure services](https://docs.microsoft.com/azure/iot-suite/iot-suite-remote-monitoring-deploy-local#deploy-the-azure-services).

* Cosmos DB
* Iot Hub
* Maps (optional)

### 2. Setup Dependencies

This service depends on the following repositories. 
Run those services from the instructions in their READMEs in the following order.

1. [Storage Adapter Microservice](https://github.com/Azure/pcs-storage-adapter-java)
1. [Authentication Microservice](https://github.com/Azure/pcs-auth-dotnet)
1. [Telemetry Microservice](https://github.com/Azure/device-telemetry-java)
1. [Device Simulation Microservice](https://github.com/Azure/device-simulation-dotnet)

### 3. Environment variables required to run the service
In order to run the service, some environment variables need to be
created at least once. See specific instructions for IDE or command
line setup below for more information. More information on environment
variables [here](#configuration-and-environment-variables).

* `PCS_STORAGEADAPTER_WEBSERVICE_URL` - the url for
  the [Storage Adapter Webservice](https://github.com/Azure/pcs-storage-adapter-java)
  used for key value storage
* `PCS_TELEMETRY_WEBSERVICE_URL` - the url for
  the [Telemetry Webservice](https://github.com/Azure/device-telemetry-java)
  used for key value storage
* `PCS_DEVICESIMULATION_WEBSERVICE_URL` - the url for
  the [Device Simulation Webservice](https://github.com/Azure/device-simulation-dotnet)
  used for key value storage
* `PCS_AZUREMAPS_KEY` - the [Azure Maps](https://azure.microsoft.com/services/azure-maps/) 
  API Key. This can be set to "static" if you do not have one.
* `PCS_AUTH_WEBSERVICE_URL` = the url for
   the [Auth Webservice](https://github.com/Azure/pcs-auth-dotnet)

# Running the service in an IDE

## Prerequisites
- Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
- Install [SBT](http://www.scala-sbt.org/download.html)
- Install the latest 
  [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads)

## Running the service with IntelliJ IDEA
IntelliJ IDEA lets you open the application without using a command
prompt, without configuring anything outside of the IDE. The SBT build tool
takes care of downloading appropriate libraries, resolving dependencies and
building the project (more info
[here](https://www.playframework.com/documentation/2.6.x/IDE)).

Steps using IntelliJ IDEA Community, with SBT plugin enabled:

* Make sure the [prerequisites](#prerequisites) are set up.
* "Open" the project with IntelliJ, the IDE should automatically recognize
  the SBT structure. Wait for the IDE to download some dependencies
  (see IntelliJ status bar). This may take a while, hang in there!
* Create a new Run Configuration, of type "SBT Task", with any name.
  * Enter `"run 9004"` (including the double quotes) in Tasks. This ensures that
   the service starts using the TCP port 9004.  If you desire to use a
    different port, feel free to change it.
  * Define the following environment variables:
    1. `PCS_STORAGEADAPTER_WEBSERVICE_URL` = http://localhost:9022/v1
    1. `PCS_DEVICESIMULATION_WEBSERVICE_URL` = http://localhost:9003/v1
    1. `PCS_TELEMETRY_WEBSERVICE_URL` = http://localhost:9004/v1
    1. `PCS_AZUREMAPS_KEY` = static
    1. `PCS_AUTH_WEBSERVICE_URL` = http://localhost:9001/v1
* Either from the toolbar or the Run menu, execute the configuration just
  created, using the Debug command/button
* Test that the service is up and running pointing your browser to
  http://127.0.0.1:9004/v1/status

## Running the service with Eclipse
The integration with Eclipse requires the
[sbteclipse plugin](https://github.com/typesafehub/sbteclipse), already
included, and an initial setup via command line (more info
[here](https://www.playframework.com/documentation/2.6.x/IDE)).

Steps using Eclipse Oxygen ("Eclipse for Java Developers" package):

* Open a console (either Bash or Windows CMD), move into the project folder,
  execute `sbt compile` and then `sbt eclipse`. This generates some files
  required by Eclipse to recognize the project.
* Open Eclipse, and from the Welcome screen "Import" an existing project,
  navigating to the root folder of the project.
* From the console run `sbt -jvm-debug 9999 "run 9004"` to start the project
* Test that the service is up and running pointing your browser to
  http://127.0.0.1:9004/v1/status
* In Eclipse, select "Run -> Debug Configurations" and add a "Remote Java
  Application", using "localhost" and port "9999".
* After saving this configuration, you can click "Debug" to connect to the
  running application.

# Running the service from the command line
1. Make sure the [prerequisites](#prerequisites) are set up.
1. Set the following environment variables in your system.
More information on environment variables
[here](#configuration-and-environment-variables).
   1. `PCS_STORAGEADAPTER_WEBSERVICE_URL` = http://localhost:9022/v1
   1. `PCS_DEVICESIMULATION_WEBSERVICE_URL` = http://localhost:9003/v1
   1. `PCS_TELEMETRY_WEBSERVICE_URL` = http://localhost:9004/v1
   1. `PCS_AZUREMAPS_KEY` = static
   1. `PCS_AUTH_WEBSERVICE_URL` = http://localhost:9001/v1
1. Use the scripts in the [scripts](scripts) folder for many frequent tasks:
   * `build`: compile all the projects and run the tests.
   * `compile`: compile all the projects.
   * `run`: compile the projects and run the service. This will prompt for
  elevated privileges in Windows to run the web service.

If you are familiar with [SBT](http://www.scala-sbt.org), you can also use SBT
directly. A copy of SBT is included in the root of the project.

## Project Structure
* **Code** for the application is in **app/com/microsoft/azure/iotsolutions/config**
    * **webservice** - web service exposing REST interface
    * **services** - business logic for interacting with [Storage Adapter] webservice
*  **Tests** are in the **test** folder
    * **WebService** - Tests for web services functionality
    * **Service** - Tests for services functionality
* Configuration files and routes are in the **conf** folder
* The **scripts** folder contains build scripts, docker container creation scripts, 
   and scripts for running the microservice from the command line

## Updating the Docker image

The `scripts` folder includes a [docker](scripts/docker) subfolder with the files
required to package the service into a Docker image:

* `build`: build a Docker container and store the image in the local registry.
* `run`: run the Docker container from the image stored in the local registry.

You might notice that there is no `Dockerfile`. All Docker settings are
defined in [build.sbt](build.sbt).

```scala
dockerRepository := Some("azureiotpcs")
dockerAlias := DockerAlias(dockerRepository.value, None, packageName.value + "-java", Some((version in Docker).value))
dockerBaseImage := "toketi/openjdk-8-jre-alpine-bash"
dockerUpdateLatest := false
dockerBuildOptions ++= Seq("--compress", "--label", "Tags=azure,iot,pcs,telemetry,Java")
dockerEntrypoint := Seq("bin/telemetry")
```

The package logic is executed via
[sbt-native-packager](https://github.com/sbt/sbt-native-packager), installed
in [plugins.sbt](project/plugins.sbt).

# Configuration and Environment variables

The service configuration is stored using Akka's
[HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md)
format in [application.conf](conf/application.conf).

The HOCON format is a human readable format, very close to JSON, with some
useful features:

* Ability to write comments
* Support for substitutions, e.g. referencing environment variables
* Supports JSON notation

The configuration file in the repository references some environment
variables that need to created at least once. Depending on your OS and
the IDE, there are several ways to manage environment variables:

* For Windows, the variables can be set [in the system][windows-envvars-howto-url]
  as a one time only task. The [env-vars-setup.cmd](scripts/env-vars-setup.cmd)
  script needs to be prepared and executed just once. When executed, the
  settings will persist across terminal sessions and reboots.
* For Linux and MacOS environments, the [env-vars-setup](scripts/env-vars-setup)
  script needs to be executed every time a new console is opened.
  Depending on the OS and terminal, there are ways to persist values
  globally, for more information these pages should help:
  * https://stackoverflow.com/questions/13046624/how-to-permanently-export-a-variable-in-linux
  * https://stackoverflow.com/questions/135688/setting-environment-variables-in-os-x
  * https://help.ubuntu.com/community/EnvironmentVariables
* IntelliJ IDEA: env. vars can be set in each 
  [Run Configuration](https://www.jetbrains.com/help/idea/run-debug-configuration-application.html)

# Contributing to the solution
Please follow our [contribution guildelines](CONTRIBUTING.md) and code style
conventions. We love PRs!

# Feedback
Please enter issues, bugs, or suggestions as 
[GitHub Issues](https://github.com/Azure/pcs-config-java/issues).

# License

Copyright (c) Microsoft Corporation. All rights reserved.
Licensed under the [MIT](LICENSE) License.

[build-badge]: https://img.shields.io/travis/Azure/pcs-config-java.svg
[build-url]: https://travis-ci.org/Azure/pcs-config-java
[issues-badge]: https://img.shields.io/github/issues/azure/pcs-config-java.svg
[issues-url]: https://github.com/azure/pcs-config-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-solutions.js.svg
[gitter-url]: https://gitter.im/azure/iot-solutions
[windows-envvars-howto-url]: https://superuser.com/questions/949560/how-do-i-set-system-environment-variables-in-windows-10
[Storage Adapter]:https://github.com/Azure/pcs-storage-adapter-java/blob/master/README.md
[rm-arch-url]:https://docs.microsoft.com/en-us/azure/iot-suite/iot-suite-remote-monitoring-sample-walkthrough
[run-with-docker-url]:https://docs.microsoft.com/azure/iot-suite/iot-suite-remote-monitoring-deploy-local#run-the-microservices-in-docker