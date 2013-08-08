import sbt._, Keys._


object BuildSettings {
  val buildOrganization = "eu.getintheloop"
  val buildVersion      = "0.0.8-SNAPSHOT"
  val buildScalaVersion = "2.10.0"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions <<= scalaVersion map { v: String =>
      val opts = "-deprecation" :: "-unchecked" :: Nil
      if (v.startsWith("2.9.")) opts else opts ++ ( "-language:implicitConversions" :: "-language:postfixOps" :: Nil)},
    crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0"),
    resolvers ++= Seq(
      "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public",
      "Shiro Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/",
      "sonatype.repo" at "https://oss.sonatype.org/content/repositories/public/"
    ),
    publishTo <<= version { (v: String) => 
      val nexus = "https://oss.sonatype.org/" 
        if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
        else Some("releases" at nexus + "service/local/staging/deploy/maven2") 
    },
    credentials ++= Seq(
      Credentials(Path.userHome / ".ivy2" / ".credentials"),
      Credentials( file("sonatype.credentials") ),
      Credentials( file("/private/liftmodules/sonatype.credentials") )
    ),
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
        <developer>
          <id>fmpwizard</id>
          <name>Diego Medina</name>
          <url>http://www.fmpwizard.com</url>
        </developer>
      </developers>)
  )
}

object LiftShiroBuild extends Build {

  val compileLiftVersion = "2.5"

  lazy val root = Project("lift-shiro-root", file("."),
    settings = BuildSettings.buildSettings ++ Seq(
      // the root is just an aggregator so dont publish a JAR
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Test, packageBin) := false,
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in (Compile, packageSrc) := false
    )) aggregate(library, example)

  import LiftModuleBuild._
  
  lazy val library: Project = Project("lift-shiro", file("library"), 
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "net.liftweb" %% "lift-webkit" % compileLiftVersion % "provided",
        "org.apache.shiro" % "shiro-core" % "1.2.0",
        "org.apache.shiro" % "shiro-web" % "1.2.0",
        "commons-beanutils" % "commons-beanutils" % "20030211.134440"
      )
    ) ++ Seq (
      liftVersion <<= liftVersion ?? "2.5-SNAPSHOT",
      liftEdition <<= liftVersion apply { _.substring(0,3) },
      name <<= (name, LiftModuleBuild.liftEdition) { (n, e) =>  n + "_" + e }
    )
  )
  
  lazy val example = Project("lift-shiro-example", file("example"),
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "net.liftweb"       %% "lift-webkit"      % compileLiftVersion % "compile",
        "net.liftmodules"   %% "fobo-jquery_2.5"  % "1.0"              % "compile",
        "org.eclipse.jetty" % "jetty-webapp"      % "7.3.0.v20110203"  % "container",
        "ch.qos.logback"    % "logback-classic"   % "0.9.26"
      )
    ) ++ com.github.siasia.WebPlugin.webSettings
  ) dependsOn library
}
