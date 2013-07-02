import sbt._, Keys._

object BuildSettings {
  val buildOrganization = "eu.getintheloop"
  val buildVersion      = "0.0.7-SNAPSHOT"
  val buildScalaVersion = "2.9.2"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    scalaVersion := "2.9.2",
    scalacOptions += "-deprecation",
    crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0"),
    resolvers ++= Seq(
      ScalaToolsReleases,
      "Shiro Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/",
      "sonatype.repo" at "https://oss.sonatype.org/content/repositories/public/"
    ),
    publishTo <<= version { (v: String) => 
      val nexus = "https://oss.sonatype.org/" 
        if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
        else Some("releases" at nexus + "service/local/staging/deploy/maven2") 
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { repo => false },
    pomExtra := (
      <url>https://github.com/timperrett/lift-shiro</url>
      <licenses>
        <license>
          <name>Apache 2.0 License</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:timperrett/lift-shiro.git</url>
        <connection>scm:git:git@github.com:timperrett/lift-shiro.git</connection>
      </scm>
      <developers>
        <developer>
          <id>timperrett</id>
          <name>Timothy Perrett</name>
          <url>http://timperrett.com</url>
        </developer>
      </developers>)
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
        "net.liftweb" %% "lift-webkit" % "2.5" % "compile",
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
