import com.sun.xml.internal.bind.v2.TODO
organization := "com.microsoft.azure.iotsolutions"

scalaVersion := "2.12.4"

lazy val telemetry = (project in file("device-telemetry"))

lazy val iothubmanager = (project in file("iothub-manager"))

val pcsconfig = (project in file("pcs-config"))

lazy val pcsstorageadapter = (project in file("pcs-storage-adapter"))

val sourceEnvVars = taskKey[Unit]("Source environment variables")

val startDonNetOnlyMs = taskKey[Unit]("Start .Net only MS")


sourceEnvVars := {
  import sys.process._

  println("Sourcing all env variables")

  lazy val projDir = baseDirectory.in(remoteMonitoring).value.getAbsolutePath

  val win = sys.props.get("os.name").get.contains("Windows")
  //TODO: Revisit to combine two if statements

  if (win) {
    Seq(projDir + "\\scripts\\local\\launch\\.env.cmd")!
  } else {
    Seq("source", projDir + "/scripts/local/launch/set_env.sh")!
  }

  if (win) {
    Seq( projDir + "\\scripts\\local\\launch\\.env_uris.cmd")!
  } else {
    Seq("source", projDir + "/scripts/local/launch/.env_uris.sh")!
  }

  println("Completed setting all env variables")
}


startDonNetOnlyMs := {
  import sys.process._

  println("Starting all .Net only MS")

  lazy val projDir = baseDirectory.in(remoteMonitoring).value.getAbsolutePath

  val win = sys.props.get("os.name").get.contains("Windows")
  if (win) {
    Seq( projDir + "\\scripts\\local\\launch\\start_dotnet_only_ms.cmd")!
  } else {
    Seq("sh", projDir + "/scripts/local/launch/start_dotnet_only_ms.sh")!
  }

  println("Completed all .Net only MS")
}


lazy val remoteMonitoring = (project in file(".")).
  settings(
    run := {
      ((run in Runtime)  dependsOn startDonNetOnlyMs).evaluated

      (run in `iothubmanager` in Compile).evaluated
      (run in `telemetry` in Compile).evaluated
      (run in `pcsconfig` in Compile).evaluated
      (run in `pcsstorageadapter` in Compile).evaluated
    }
  )
