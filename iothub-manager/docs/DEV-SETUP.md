Development configuration for developers
========================================

The project contains .NET and Java source code for the same microservice, 
however you don't need to use both of them. Feel free to select the 
programming language closer to your target platform. 

Note: .NET Core is not supported yet, all .NET development is currently done
using .NET Framework on Windows and Mono on Linux/MacOS.

It is our goal to support both .NET and Java versions, for instance when
adding new features we will update both codebases.

The steps below will help to setup your development environment.

.NET setup
==========

1. Install [.NET Core](https://dotnet.github.io/)
2. MacOS/Linux: Install [Mono 5.x](http://www.mono-project.com/download/alpha/)
3. Some IDE options:
   * [Visual Studio](https://www.visualstudio.com/)
   * [IntelliJ Rider](https://www.jetbrains.com/rider) 
   * [Visual Studio Code](https://code.visualstudio.com/)
   * [Visual Studio for Mac](https://www.visualstudio.com/vs/visual-studio-mac)
   
You might notice a `dotnet4x` and `dotnet` folder:

* **dotnet4x** : code targeting .NET Framework
* **dotnet** : code targeting .NET Core, currently not in use 

Some scripts are included for Windows/Linux/MacOS, to execute common steps:

* Build: `dotnet4x\scripts\build`
* Run: `dotnet4x\scripts\run`
* Create Docker container: `dotnet4x\scripts\docker-build`
* Run Docker container: `dotnet4x\scripts\docker-run`

Java setup
==========

1. Install the latest Java SDK.
2. Optionally install Gradle, or feel free to use the included Gradle wrapper (`gradlew`).
3. Use your preferred IDE, IntelliJ IDEA and Eclipse are the most popular, however anything will work.

Some scripts are included for Windows/Linux/MacOS, to execute common steps:

* Build: `java/scripts/build`
* Run: `java/scripts/run`
* Create Docker container: `java/scripts/docker-build`
* Run Docker container: `java/scripts/docker-run`

IoT Hub setup
=============

At some point you will probably want to setup your Azure IoT Hub, for development and integration tests.

The project includes some Bash scripts to help you with this setup:

* Create new IoT Hub: `./scripts/iothub/create-hub.sh`
* List existing hubs: `./scripts/iothub/list-hubs.sh`
* Show IoT Hub details (e.g. keys): `./scripts/iothub/show-hub.sh`

and in case you had multiple Azure subscriptions:

* Show subscriptions list: `./scripts/iothub/list-subscriptions.sh`
* Change current subscription: `./scripts/iothub/select-subscription.sh`

Git setup
=========

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

Other documents
===============

* [.NET Build and Run](../dotnet/README.md)
* [Java Build and Run](../java/README.md)
* [Project customization](CUSTOMIZE.md)
* [Docker](DOCKER.md)
* [Continuous Integration](CI.md)
