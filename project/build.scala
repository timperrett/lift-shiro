import sbt._, Keys._

object BuildSettings {
  val buildOrganization = "eu.getintheloop"
  val buildVersion      = "0.0.5"
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
      "Shiro Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/"
    ),
    publishTo <<= version { (v: String) => 
      val nexus = "https://oss.sonatype.org/" 
        if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
        else Some("releases" at nexus + "service/local/staging/deploy/maven2") 
    }, 
    publishMavenStyle := true,
    publishArtifact in Test := false
  )
}

object LiftShiroBuild extends Build {
  lazy val root = Project("lift-shiro-root", file("."),
    settings = BuildSettings.buildSettings ++ Seq(
      // the root is just an aggregator so dont publish a JAR
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Test, packageBin) := false,
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in (Compile, packageSrc) := false
    )
  ) aggregate(library, example)
  
  lazy val library: Project = Project("lift-shiro", file("library"), 
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "net.liftweb" %% "lift-webkit" % "2.4" % "compile",
        "org.apache.shiro" % "shiro-core" % "1.2.0",
        "org.apache.shiro" % "shiro-web" % "1.2.0",
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
