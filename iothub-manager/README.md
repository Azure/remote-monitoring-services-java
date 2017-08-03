[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]
[![Gitter][gitter-badge]][gitter-url]

IoTHubManager
=================

Handles communication with the IoT Hub (device registration, device queries, etc.).

Overview
========

* code for the application is in app/com.microsoft.azure.iotsolutions.iothubmanager/
* WebService - Java web service exposing REST interface for IoT Hub management functionality
* Services - Java project containing business logic for interacting with Azure services (IoTHub, etc.)

* tests are in test/com.microsoft.azure.iotsolutions.iothubmanager/
* WebService - Tests for web services functionality
* Service - Tests for services functionality

* scripts - contains build scripts, docker container creation scripts, and scripts for running the microservice from the command line

* routes - defines the URL mapping to web service classes

How to use it
=============

For Running tests in Intellij:
1. Open the file with the test you want to run; e.g. test/com.microsoft.azure.iotsolutions.iothubmanager/WebService/Runtime/ConfigTest
2. Click the play button next to the test you want to execute.

For Debugging in Intellij:
1. Set up your PCS_IOTHUB_CONNSTRING system environment variable for your IoT Hub connection.
2. Setup a run debug SBT configuration for Intellij: Tasks - "run 8080", Single Instance only checked,
working directory: /source/PCS2/iothub-manager-java.
3. Hit the REST api for the web service using:
	* http://127.0.0.1:8080/v1/status (checks status of the web service)
	* http://127.0.0.1:8080/v1/devices (queries for all devices)
	* http://127.0.0.1:8080/v1/devices/<yourindividualdevice> (queries for a single device)
	* <todo - create device>
	* <todo - create device>

Using Swagger:
1. <todo - Swagger>

Running locally in a container:
1. <todo - container instructions>

Running on Azure in a container in ACS:
1. <todo - cloud environment container instructions>


Configuration
=============

1. * application.conf - contains configuration for the web service (port, hostname, and IoT hub connection string environment variable name)
2. PCS_IOTHUB_CONNSTRING is a system environment variable and should contain your IoT Hub connection string. Create this environment variable before running the microservice.
3. <todo - logging/monitoring>


Other documents
===============

* [Contributing and Development setup](CONTRIBUTING.md)
* [Development setup, scripts and tools](DEVELOPMENT.md)
* <todo - architecture docs link>
* <todo - doc pointing to overarching doc for how this microservice is used in remote monitoring and other PCS types>


[build-badge]: https://img.shields.io/travis/Azure/PROJECT-ID-HERE-java.svg
[build-url]: https://travis-ci.org/Azure/PROJECT-ID-HERE-java
[issues-badge]: https://img.shields.io/github/issues/azure/PROJECT-ID-HERE-java.svg
[issues-url]: https://github.com/azure/PROJECT-ID-HERE-java/issues
[gitter-badge]: https://img.shields.io/gitter/room/azure/iot-pcs.js.svg
[gitter-url]: https://gitter.im/azure/iot-pcs
