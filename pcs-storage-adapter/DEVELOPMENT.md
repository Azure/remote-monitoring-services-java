* [Run and Debug with IntelliJ](#run-and-debug-with-intellij)
* [Run and Debug with Eclipse](#run-and-debug-with-eclipse)
* [Build & Run from the command line](#build--run-from-the-command-line)
* [Package the application to a Docker image](#package-the-application-to-a-docker-image)
* [Service configuration](#configuration)
* [Azure IoT Hub setup](#azure-iot-hub-setup)
* [Development setup](#development-setup)

Configuration and Environment variables
=======================================

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
* IntelliJ IDEA: env. vars can be set in each Run Configuration, see
  https://www.jetbrains.com/help/idea/run-debug-configuration-application.html

Run and Debug with IntelliJ
===========================

Intellij IDEA lets you quickly open the application without using a command
prompt, without configuring anything outside of the IDE. The SBT build tool
takes care of downloading appropriate libraries, resolving dependencies and
building the project (more info
[here](https://www.playframework.com/documentation/2.6.x/IDE)).

Steps using IntelliJ IDEA Community 2017, with SBT plugin enabled:

* "Open" the project with IntelliJ, the IDE should automatically recognize the
  SBT structure. Wait for the IDE to download some dependencies (see IntelliJ
  status bar)
* Create a new Run Configuration, of type "SBT Task" and enter "run 900X"
  (including the double quotes). This ensures that the service to start using
  the TCP port 900X
* Either from the toolbar or the Run menu, execute the configuration just
  created, using the Debug command/button
* Test that the service is up and running pointing your browser to
  http://127.0.0.1:900X/v1/status

And as a nice extra: if you edit the code, you don't need to stop/build/restart
the application. Play and SBT automatically recompile the files modified on the
fly. You can also see the re-build log in the Run Tool Window.

Run and Debug with Eclipse
==========================

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
* From the console run `sbt -jvm-debug 9999 "run 900X"` to start the project
* Test that the service is up and running pointing your browser to
  http://127.0.0.1:900X/v1/status
* In Eclipse, select "Run -> Debug Configurations" and add a "Remote Java
  Application", using "localhost" and port "9999".
* After saving this configuration, you can click "Debug" to connect to the
  running application.

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

### Sandbox

The scripts assume that you configured your development environment,
with tools like .NET Core and Docker. You can avoid installing .NET Core,
and install only Docker, and use the command line parameter `--in-sandbox`
(or the short form `-s`), for example:

* `build --in-sandbox`: executes the build task inside of a Docker
    container (short form `build -s`).
* `compile --in-sandbox`: executes the compilation task inside of a Docker
    container (short form `compile -s`).
* `run --in-sandbox`: starts the service inside of a Docker container
    (short form `run -s`).

The Docker images used for the sandbox is hosted on Docker Hub
[here](https://hub.docker.com/r/azureiotpcs/code-builder-java).

Package the application to a Docker image
=========================================

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
dockerBuildOptions ++= Seq("--squash", "--compress", "--label", "Tags=Azure,IoT,PCS,Java")
dockerEntrypoint := Seq("bin/storage-adapter")
```

The package logic is executed via
[sbt-native-packager](https://github.com/sbt/sbt-native-packager), installed
in [plugins.sbt](project/plugins.sbt).

Azure IoT Hub setup
===================

To use the microservice you will need to setup your Azure IoT Hub,
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

We also provide a
[.NET version](https://github.com/Azure/pcs-storage-adapter-dotnet)
of this project and other Azure IoT PCS components.

## Git setup

The project includes a Git hook, to automate some checks before accepting a
code change. You can run the tests manually, or let the CI platform to run
the tests. We use the following Git hook to automatically run all the tests
before sending code changes to GitHub and speed up the development workflow.

If at any point you want to remove the hook, simply delete the file installed
under `.git/hooks`. You can also bypass the pre-commit hook using the
`--no-verify` option.

#### Pre-commit hook with sandbox

To setup the included hooks, open a Windows/Linux/MacOS console and execute:

```
cd PROJECT-FOLDER
cd scripts/git
setup --with-sandbox
```

With this configuration, when checking in files, git will verify that the
application passes all the tests, running the build and the tests inside
a Docker container configured with all the development requirements.

#### Pre-commit hook without sandbox

Note: the hook without sandbox requires Java JDK utilities in the system PATH.

To setup the included hooks, open a Windows/Linux/MacOS console and execute:

```
cd PROJECT-FOLDER
cd scripts/git
setup --no-sandbox
```

With this configuration, when checking in files, git will verify that the
application passes all the tests, running the build and the tests in your
workstation, using the tools installed in your OS.

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
