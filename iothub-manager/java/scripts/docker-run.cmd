@ECHO off

SET DOCKER_IMAGE="azureiotpcs/microservice-template-java-ws:0.1-SNAPSHOT"

echo Starting web service at: http://localhost:8080

docker run -it -p 8080:8080 %DOCKER_IMAGE%
