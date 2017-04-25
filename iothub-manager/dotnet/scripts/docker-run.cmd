@ECHO off

SET DOCKER_IMAGE="azureiotpcs/microservice-template-dotnet-ws:0.1-SNAPSHOT"

echo Starting web service at: http://localhost:8080/api/values

docker run -it -p 8080:80 %DOCKER_IMAGE%
