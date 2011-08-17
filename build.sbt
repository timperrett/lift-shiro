
organization := "eu.getintheloop"

name := "lift-shiro"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.9.0-1"

crossScalaVersions := Seq("2.8.1", "2.9.0", "2.9.0-1")

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % "2.4-M2",
  "org.apache.shiro" % "shiro-core" % "1.2.0-SNAPSHOT",
  "org.apache.shiro" % "shiro-web" % "1.2.0-SNAPSHOT",
  "commons-beanutils" % "commons-beanutils" % "20030211.134440"
)

resolvers += Resolver.file(".m2", file(Path.userHome+"/.m2/repository"))

publishTo <<= version { (v: String) =>
  val nexus = "http://nexus.scala-tools.org/content/repositories/"
  if(v endsWith "-SNAPSHOT") Some("Scala Tools Nexus" at nexus + "snapshots/")
  else Some("Scala Tools Nexus" at nexus + "releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
