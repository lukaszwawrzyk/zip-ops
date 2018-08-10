name := "zip-ops"

version := "0.1"

scalaVersion := "2.12.5"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "net.lingala.zip4j" % "zip4j" % "1.3.2",
  "org.openjdk.jmh" % "jmh-core" % "1.21",
  "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.21",
  "org.scala-sbt" %% "io" % "1.2.1"
)

enablePlugins(JmhPlugin)