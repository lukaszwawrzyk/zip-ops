name := "zip-ops"

version := "0.1"

scalaVersion := "2.12.5"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "net.lingala.zip4j" % "zip4j" % "1.3.2"
