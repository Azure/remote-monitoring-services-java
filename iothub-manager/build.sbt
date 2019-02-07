// Copyright (c) Microsoft. All rights reserved.
import sbt.project

name := "iothub-manager"
organization := "com.microsoft.azure.iotsolutions"
scalaVersion := "2.12.4"

libraryDependencies ++= {
  Seq(
    filters,
    guice,
    ws,

    // https://github.com/Azure/azure-iot-sdk-java/releases
    "com.microsoft.azure.sdk.iot" % "iot-service-client" % "1.15.1",

    // https://mvnrepository.com/artifact/com.nimbusds/oauth2-oidc-sdk
    "com.nimbusds" % "oauth2-oidc-sdk" % "5.36",

    "org.apache.httpcomponents" % "httpclient" % "4.5.3",

    "io.netty" % "netty-all" % "4.1.0.Final"
  )
}

// Test dependencies
libraryDependencies ++= Seq(
  // http://search.maven.org/#search%7Cga%7C1%7Cmockito-core
  "org.mockito" % "mockito-core" % "2.8.47" % "test",
  // https://github.com/Azure/azure-iot-sdk-java/releases
  "com.microsoft.azure.sdk.iot" % "iot-device-client" % "1.6.0" % "test",
  // https://github.com/Pragmatists/JUnitParams
  "pl.pragmatists" % "JUnitParams" % "1.1.1" % "test"
)

lazy val commonSettings = Seq(
  version := "testing",

  organizationName := "Microsoft Azure",
  organizationHomepage := Some(new URL("https://www.microsoft.com/internet-of-things/azure-iot-suite")),
  homepage := Some(new URL("https://www.microsoft.com/internet-of-things/azure-iot-suite")),
  startYear := Some(2017),

  // Assembly
  assemblyMergeStrategy in assembly := {
    case m if m.startsWith("META-INF") ⇒ MergeStrategy.discard
    case m if m.contains(".txt")       ⇒ MergeStrategy.discard
    case x                             ⇒ (assemblyMergeStrategy in assembly).value(x)
  },

  // Publishing options, see http://www.scala-sbt.org/0.13/docs/Artifacts.html
  licenses += ("MIT", url("https://github.com/Azure/pcs-template-microservice-java/blob/master/LICENSE")),
  publishMavenStyle := true,
  publishArtifact in Test := true,
  publishArtifact in(Compile, packageDoc) := true,
  publishArtifact in(Compile, packageSrc) := true,
  publishArtifact in(Compile, packageBin) := true,

  // Test
  testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),

  // Misc
  logLevel := Level.Info, // Debug|Info|Warn|Error
  scalacOptions ++= Seq("-deprecation", "-explaintypes", "-unchecked", "-feature"),
  showSuccess := true,
  showTiming := true,
  logBuffered := false,
  fork := true,
  parallelExecution := true
)

// Main module
lazy val iothubmanager = project.in(file("."))
  .enablePlugins(PlayJava)
  .configs(IntegrationTest)
  .settings(commonSettings)

// Play framework
PlayKeys.externalizeResources := false
PlayKeys.playDefaultPort := 9002

// Docker
// Note: use lowercase name for the Docker image details
enablePlugins(JavaAppPackaging)
dockerRepository := Some("azureiotpcs")
dockerAlias := DockerAlias(dockerRepository.value, None, packageName.value + "-java", Some((version in Docker).value))
maintainer in Docker := "Devis Lucato (https://github.com/dluc)"
dockerBaseImage := "toketi/openjdk-8-jre-alpine-bash"
dockerUpdateLatest := false
dockerBuildOptions ++= Seq("--compress", "--label", "Tags=Azure,IoT,Solutions,IoT Hub,Java")
// Example params: -Dconfig.file=/opt/conf/prod.conf -Dhttp.port=1234 -Dhttp.address=127.0.0.1
dockerEntrypoint := Seq("bin/iothub-manager")
