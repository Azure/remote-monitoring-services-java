organization := "com.microsoft.azure.iotsolutions"

scalaVersion := "2.12.4"

lazy val telemetry = (project in file("device-telemetry"))

lazy val iothubmanager = (project in file("iothub-manager"))

lazy val pcsconfig = (project in file("pcs-config"))

lazy val pcsstorageadapter = (project in file("pcs-storage-adapter"))

val startDonNetOnlyMs = taskKey[Unit]("Start .Net only MS")

startDonNetOnlyMs := {
  import sys.process._

  lazy val projDir = baseDirectory.in(remoteMonitoring).value.getAbsolutePath

  println("Starting all .Net only MS")

  val os = sys.props.get("os.name").get.contains("Windows")
  if (os) {
    Seq(projDir + "\\scripts\\local\\launch\\start_dotnet_only_ms.cmd")!
  } else {
    Seq("sh", projDir + "/scripts/local/launch/start_dotnet_only_ms.sh")!
  }
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
