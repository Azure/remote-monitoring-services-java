[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

Device Telemetry Overview
=========================

This service offers read access to device telemetry, full CRUD for rules, and read/write for
 alarms from storage for the client via a RESTful endpoint.

## Features the microservice offers:

1. Gets a list of telemetry messages for specific parameters
1. Gets a list of alarms for specific parameters
1. Gets a single alarm
1. Modifies alarm status
1. Create/Read/Update/Delete Rules
    1. Create Rules
    1. Gets a list of rules for specific parameters
    1. Gets a single rule
    1. Modify existing rule
    1. Delete existing rule

Dependencies
============

1. DocumentDB Storage
1. [Storage Adapter Webservice](https://github.com/Azure/pcs-storage-adapter-dotnet)

How to use the microservice
===========================

## Local Setup

### 1. Environment Variables

Run `scripts\env-vars-setup.cmd` on Windows or `source scripts\env-vars-setup`
on Mac/Linux to set up the environment variables needed to run the service locally.
If using envornemnt variables, this service requires the following environment
variables to be set:
- PCS_TELEMETRY_DOCUMENTDB_CONNSTRING - the connection string for the Azure DocumentDB backend
- PCS_STORAGEADAPTER_WEBSERVICE_URL - the url for
  the [Storage Adapter Webservice](https://github.com/Azure/pcs-storage-adapter-dotnet)
  used for key value storage

## Quickstart - Running the service with Docker

--todo

## Running the service in an IDE

### Prerequisites

- Install SBT: http://www.scala-sbt.org/download.html
- Install the latest Java SDK: http://www.oracle.com/technetwork/java/javase/downloads/index.html

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

* **Code** for the application is in app/com.microsoft.azure.iotsolutions.telemetry/
    * **WebService** - Java web service exposing REST interface for managing Ruels,
    Alarms, and Messages
    * **Services** - Java project containing business logic for interacting with
    storage service
* **Tests** are in the test folder
    * **WebService** - Tests for web services functionality
    * **Service** - Tests for services functionality
* **Scripts** - contains build scripts and docker container creation scripts
* **Routes** - defines the URL mapping to web service classes


Build & Run from the command line
=================================

The [scripts](scripts) folder includes some scripts for frequent tasks you
might want to run from the command line:

* `compile`: compile the project.
* `build`: compile the project and run the tests.
* `run`: compile the project and run the service.

The scripts check for the environment variables setup. You can set the
environment variables globally in your OS, or use the "env-vars-setup"
script in the scripts folder.

If you are familiar with [SBT](http://www.scala-sbt.org), you can also use SBT
directly. A copy of SBT is included in the root of the project.


Updating the Docker image
=========================

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


Contributing to the solution
============================

Please follow our contribution guildelines and code style conventions defined in [guidelines](https://github.com/Azure/iothub-manager-java/blob/master/CONTRIBUTING.md).

Feedback
========

Please enter issues, bugs, or suggestions as GitHub Issues here: https://github.com/Azure/device-telemetry-java/issues.

Other documents
===============

* [Contributing and Development setup](CONTRIBUTING.md)
* [Development setup, scripts and tools](DEVELOPMENT.md)

[build-badge]: https://img.shields.io/travis/Azure/device-telemetry-java.svg
[build-url]: https://travis-ci.org/Azure/device-telemetry-java
[issues-badge]: https://img.shields.io/github/issues/azure/device-telemetry-java.svg
[issues-url]: https://github.com/azure/device-telemetry-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-solutions.js.svg
[gitter-url]: https://gitter.im/azure/iot-solutions
