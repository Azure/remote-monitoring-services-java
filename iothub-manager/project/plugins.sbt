// Copyright (c) Microsoft. All rights reserved.

logLevel := Level.Warn

resolvers += Classpaths.sbtPluginReleases

// Play framework
// - https://github.com/playframework/playframework/tags
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.14")

// `sbt assembly`
// - https://github.com/sbt/sbt-assembly/releases
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4")

// Docker (Note: in case of problems, try disabling sbt assembly)
// - https://github.com/sbt/sbt-native-packager/releases
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.2.0-M9")
