[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

Device Telemetry
=================

This service gets device telemetry, rules, and alarms from storage for the client
via a RESTful endpoint. This service also allows clients to modify alarms, and
create/modify/delete rules.

Overview
========

* **Code** for the application is in app/com.microsoft.azure.iotsolutions.devicetelemetry/
    * **WebService** - Java web service exposing REST interface for managing Ruels,
    Alarms, and Messages
    * **Services** - Java project containing business logic for interacting with
    storage service
* **Tests** are in the test folder
    * **WebService** - Tests for web services functionality
    * **Service** - Tests for services functionality
* **Scripts** - contains build scripts and docker container creation scripts
* **Routes** - defines the URL mapping to web service classes

How to use it
=============
## Local Setup
### 1. Enviornment Variables
Run `scripts\env-vars-setup.cmd` on Windows or `source scripts\env-vars-setup`
on Mac/Linux to set up the environment variables needed to run the service locally.
If using envornemnt variables, this service requires the following environment
variables to be set:
- PCS_DEVICETELEMETRY_DOCUMENTDB_CONNSTRING - the connection string for the Azure DocumentDB backend
- PCS_STORAGEADAPTER_WEBSERVICE_URL - the url for
  the [Storage Adapter Webservice](https://github.com/Azure/pcs-storage-adapter-dotnet)
  used for key value storage
- PCS_DEVICETELEMETRY_WEBSERVICE_PORT - the port number (integer) used by this
  service: 9004

## Features
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

### Dependencies
1. DocumentDB Storage
1. [Storage Adapter Webservice](https://github.com/Azure/pcs-storage-adapter-dotnet)

Other documents
===============

* [Contributing and Development setup](CONTRIBUTING.md)
* [Development setup, scripts and tools](DEVELOPMENT.md)

[build-badge]: https://img.shields.io/travis/Azure/device-telemetry-java.svg
[build-url]: https://travis-ci.org/Azure/device-telemetry-java
[issues-badge]: https://img.shields.io/github/issues/azure/device-telemetry-java.svg
[issues-url]: https://github.com/azure/device-telemetry-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-pcs.js.svg
[gitter-url]: https://gitter.im/azure/iot-pcs
