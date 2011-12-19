import sbt._, Keys._

object BuildSettings {
  val buildOrganization = "eu.getintheloop"
  val buildVersion      = "0.0.5-SNAPSHOT"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    scalaVersion := "2.9.1",
    scalacOptions += "-deprecation",
    crossScalaVersions := Seq("2.8.1", "2.9.0", "2.9.0-1", "2.9.1"),
    resolvers ++= Seq(
      ScalaToolsReleases,
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/"//,
      // ".m2" at "file://"+Path.userHome+"/.m2/repository"
    ),
    publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )
}

object LiftShiroBuild extends Build {
  lazy val root = Project("lift-shiro-root", file("."),
    settings = BuildSettings.buildSettings
  ) aggregate(library, example)
  
  lazy val library: Project = Project("lift-shiro", file("library"), 
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "net.liftweb" %% "lift-webkit" % "2.4-M5" % "compile",
        "org.apache.shiro" % "shiro-core" % "1.2.0-SNAPSHOT",
        "org.apache.shiro" % "shiro-web" % "1.2.0-SNAPSHOT",
        "commons-beanutils" % "commons-beanutils" % "20030211.134440"
      )
    )
  )
  
  lazy val example = Project("lift-shiro-example", file("example"),
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "container",
        "ch.qos.logback" % "logback-classic" % "0.9.26"
      )
    ) ++ com.github.siasia.WebPlugin.webSettings
  ) dependsOn(library)
}
