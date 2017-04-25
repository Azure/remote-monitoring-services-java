.NET
====

## Customizing the Docker container details

The tag applied to the Docker images is stored in the scripts used to create the images.

Look for the "DOCKER_IMAGE" variables in [`dotnet/scripts/docker-*.*`](../dotnet/scripts/) files.

Java
====

## Changing the project details

The project and container name is stored in [`settings.gradle`](../java/settings.gradle):

```
rootProject.name = 'microservice-template-java-ws'
```

The JVM artifact group name is stored in [`build.gradle`](../java/build.gradle):

```
group 'com.microsoft.azure.iot'
```

## Customizing the Docker container details

The container details are stored in [`build.gradle`](../java/build.gradle):

```
distDocker {
    tag "azureiotpcs/device-simulation-java"
    baseImage "openjdk:latest"
    maintainer "Devis Lucato (https://github.com/dluc)"
    exposePort 8080
}
```

## Changing the service configuration

The service configuration is stored in [`application.conf`](../java/src/main/resources/application.conf).

