
[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

Config service Overview
=======================
This service handles communication with the [Storage Adapter] microservice to complete the its tasks.

The microservice provides a RESTful endpoint to make CRUD operation for "devicegroups","solution-settings" and "user-settings". The data will be stored in [Azure DocumentDB] by [Storage Adapter] microservice.

Dependencies
============
- [Storage adapter microservice] used to store data

How to use the microservice
===========================
## Local Setup

### 1. Environment Variables

Run `scripts\env-vars-setup.cmd` on Windows or `source scripts\env-vars-setup`
on Mac/Linux to set up the environment variables needed to run the service locally.
If using environment variables, this service requires the following environment
variables to be set:
- `PCS_STORAGEADAPTER_WEBSERVICE_URL` - the url for
  the [Storage Adapter Webservice](https://github.com/Azure/pcs-storage-adapter-java)
  used for key value storage

## Quickstart - Running the service with Docker
You can quickly start the Config service and its dependencies in one simple step, using Docker Compose with the
[docker-compose.yml](scripts/docker/docker-compose.yml) file in the project:

```
cd scripts/docker
docker-compose up
```

The Docker compose configuration requires the `PCS_STORAGEADAPTER_WEBSERVICE_URL` environment variable.

Running the service in an IDE
=============================
## Prerequisites
- Install SBT: http://www.scala-sbt.org/download.html
- Install the latest Java SDK:
  http://www.oracle.com/technetwork/java/javase/downloads/index.html

## Run and Debug with IntelliJ

Install Intellij IDEA Community: https://www.jetbrains.com/idea/download

Intellij IDEA lets you quickly open the application without using a command
prompt, without configuring anything outside of the IDE. The SBT build tool
takes care of downloading appropriate libraries, resolving dependencies and
building the project (more info
[here](https://www.playframework.com/documentation/2.6.x/IDE)).

Steps using IntelliJ IDEA Community 2017, with SBT plugin enabled:

* "Open" the project with IntelliJ, the IDE should automatically recognize the
  SBT structure. Wait for the IDE to download some dependencies (see IntelliJ
  status bar)
* Create a new Run Configuration, of type "SBT Task" and enter "run 9004"
  (including the double quotes). This ensures that the service to start using
  the TCP port 9004
* Either from the toolbar or the Run menu, execute the configuration just
  created, using the Debug command/button
* Test that the service is up and running pointing your browser to
  http://127.0.0.1:9004/v1/status

And as a nice extra: if you edit the code, you don't need to stop/build/restart
the application. Play and SBT automatically recompile the files modified on the
fly. You can also see the re-build log in the Run Tool Window.

## Run and Debug with Eclipse

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

Project Structure
=================
This microservice contains the following projects:
* **app/com/microsoft/azure/iotsolutions/config**
    * **webservice** - web service exposing REST interface
    * **services** - business logic for interacting with [Storage Adapter] webservice
* **test/com/microsoft/azure/iotsolutions/config**
    * **webservice** - tests for webservice functionality
    * **services** - tests for services functionality
* **conf** - configuration files and routes
* **scripts** - contains build scripts, docker container creation scripts,
   and scripts for running the microservice from the command line
* **routes** - defines the URL mapping to web service classes

Build and Run from the command line
===================================
The [scripts](scripts) folder contains scripts for many frequent tasks:

* `compile`: compile the projects.
* `build`: compile the projects and run the tests.
* `run`: compile the projects and run the service. This will prompt for
  elevated privileges in Windows to run the web service.

The scripts check for the environment variables setup. You can set the
environment variables globally in your OS, or use the "env-vars-setup"
script in the scripts folder.

If you are familiar with [SBT](http://www.scala-sbt.org), you can also use SBT
directly. A copy of SBT is included in the root of the project.

Updating the Docker image
=========================

The `scripts` folder includes a [docker](scripts/docker) subfolder with the
files required to package the service into a Docker image:

* `build`: build a Docker container and store the image in the local registry
* `run`: run the Docker container from the image stored in the local registry

You might notice that there is no `Dockerfile`. All Docker settings are
defined in [build.sbt](build.sbt).

```Scala
dockerRepository := Some("azureiotpcs")
dockerAlias := DockerAlias(dockerRepository.value, None, packageName.value + "-java", Some((version in Docker).value))
maintainer in Docker := "Devis Lucato (https://github.com/dluc)"
dockerBaseImage := "toketi/openjdk-8-jre-alpine-bash"
dockerUpdateLatest := true
dockerBuildOptions ++= Seq("--compress", "--label", "Tags=Azure,IoT,PCS,Java")
dockerEntrypoint := Seq("bin/pcs-config")
```
The package logic is executed via
[sbt-native-packager](https://github.com/sbt/sbt-native-packager), installed
in [plugins.sbt](project/plugins.sbt).


Service Configuration
=======================================
The service configuration is stored in
[`conf/application.conf`](conf/application.conf). The format allows to store
values in a readable format, with comments. The application also supports inserting environment variables, such as credentials and networking details.

The configuration file [`conf/application.conf`](conf/application.conf) references the environment variable `PCS_STORAGEADAPTER_WEBSERVICE_URL` that is required to be created at least once. Depending on your OS and the IDE, there are several ways to manage environment variables:

Contributing to the solution
============================
Please follow our [contribution guildelines](CONTRIBUTING.md) and code style
conventions.

Feedback
========
Please enter issues, bugs, or suggestions as GitHub Issues here:
https://github.com/Azure/pcs-config-java/issues.

[build-badge]: https://img.shields.io/travis/Azure/pcs-config-java.svg
[build-url]: https://travis-ci.org/Azure/pcs-config-java
[issues-badge]: https://img.shields.io/github/issues/azure/pcs-config-java.svg
[issues-url]: https://github.com/azure/pcs-config-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-solutions.js.svg
[gitter-url]: https://gitter.im/azure/iot-solutions

[Storage Adapter]:https://github.com/Azure/pcs-storage-adapter-java/blob/master/README.md
[Azure DocumentDB]:(https://ms.portal.azure.com/#create/Microsoft.DocumentDB)
