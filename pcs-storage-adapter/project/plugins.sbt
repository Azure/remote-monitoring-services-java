// Copyright (c) Microsoft. All rights reserved.

logLevel := Level.Warn

resolvers += Classpaths.sbtPluginReleases

// Play framework
// - https://github.com/playframework/playframework/tags
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.1")

// `sbt assembly`
// - https://github.com/sbt/sbt-assembly/releases
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

// Docker (Note: in case of problems, try disabling sbt assembly)
// - https://github.com/sbt/sbt-native-packager/releases
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.2.0")

// Integration with Eclipse
// - https://www.playframework.com/documentation/2.6.x/IDE
// Note: when changing version, make sure `sbt eclipse` works in a new clone,
//       e.g. version 5.x doesn't work as of June 2017.
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")
