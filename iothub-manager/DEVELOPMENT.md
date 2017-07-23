* [Build, Run locally and with Docker](#build-run-locally-and-with-docker)
* [Service configuration](#configuration)
* [Azure IoT Hub setup](#azure-iot-hub-setup)
* [Development setup](#development-setup)

Build, Run locally and with Docker
==================================

The [scripts](scripts) folder includes some scripts for frequent tasks:

* `build`: compile the project and run the tests.
* `run`: compile the project and run the service.
* `docker-build`: build a Docker container and store the image in the local
  registry.
* `docker-run`: run the Docker container from the image stored in the local
  registry.

Configuration
=============

The service configuration is stored using Akka's
[HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md)
format in `application.conf`.

The HOCON format is a human readable format, very close to JSON, with some
useful features:

* Ability to write comments
* Support for substitutions, e.g. referencing environment variables
* Supports JSON notation

Azure IoT Hub setup
===================

To use IoT Hub Manager you will need to setup your Azure IoT Hub,
for development and integration tests.

The project includes some Bash scripts to help you with this setup:

* Create new IoT Hub: `./scripts/iothub/create-hub.sh`
* List existing hubs: `./scripts/iothub/list-hubs.sh`
* Show IoT Hub details (e.g. keys): `./scripts/iothub/show-hub.sh`

and in case you had multiple Azure subscriptions:

* Show subscriptions list: `./scripts/iothub/list-subscriptions.sh`
* Change current subscription: `./scripts/iothub/select-subscription.sh`

Development setup
=================

## Java setup

1. Install the latest Java SDK.
2. Use your preferred IDE,
   [IntelliJ IDEA](https://www.jetbrains.com/idea/) and
   [Eclipse](https://www.eclipse.org) are the most popular,
   however anything should be just fine.

We provide also a
[.NET version](https://github.com/Azure/iothub-manager-dotnet)
of this project and other Azure IoT PCS components.

## Git setup

The project includes a Git hook, to automate some checks before accepting a
code change. You can run the tests manually, or let the CI platform to run
the tests. We use the following Git hook to automatically run all the tests
before sending code changes to GitHub and speed up the development workflow.

To setup the included hooks, open a Windows/Linux/MacOS console and execute:

```
cd PROJECT-FOLDER
cd scripts/git
setup
```

If at any point you want to remove the hook, simply delete the file installed
under `.git/hooks`. You can also bypass the pre-commit hook using the
`--no-verify` option.

## Code style

If you use IntelliJr, you can load the code style settings from the repository,
stored in [intellij-code-style.xml](intellij-code-style.xml).

Some quick notes about the project code style:

1. Where reasonable, lines length is limited to 80 chars max, to help code
   reviews and command line editors.
2. Code blocks indentation with 4 spaces. The tab char should be avoided.
3. Text files use Unix end of line format (LF).
4. Dependency Injection is managed with
   [Guice](https://github.com/google/guice/wiki/GettingStarted).
5. Web service APIs fields are CamelCased (except for metadata).
