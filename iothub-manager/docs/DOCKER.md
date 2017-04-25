Build container image
=====================

To build the Docker image and publish it to your local repository:

## .NET Framework

`dotnet4x/scripts/docker-build`

will generate a new image with name `azureiotpcs/microservice-template-dotnet4x-ws` and
publish it to your local repository.

## .NET Core

`dotnet/scripts/docker-build`

will generate a new image with name `azureiotpcs/microservice-template-dotnet-ws` and
publish it to your local repository.

## Java

`java/scripts/docker-build` or `gradle distDocker`

will generate a new image with name `azureiotpcs/microservice-template-java-ws` and
publish it to your local repository.

Deploy container with Docker
============================

## .NET Framework

`dotnet4x/scripts/docker-run`

will start the service, that you can test opening the browser at 

> http://localhost:8080/api/values

## .NET Core

`dotnet/scripts/docker-run`

will start the service, that you can test opening the browser at 

> http://localhost:8080/api/values

## Java

`java/scripts/docker-run`

will start the service, that you can test opening the browser at 

> http://127.0.0.1:8080/devices/102354

Publishing to Docker Hub
========================

Our public repository: https://hub.docker.com/u/azureiotpcs

```
docker login -u azureiotpcs
```

Password: *****

```
docker push azureiotpcs/<NAME>
```

You can also work with your own repository, for instance if your docker hub account is "my-dockerhub-user":

```
docker login -u my-dockerhub-user
docker tag <image id> my-dockerhub-user/my-service
docker push foo/my-service
```
