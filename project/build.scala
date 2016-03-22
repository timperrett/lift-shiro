import sbt._, Keys._
import LiftModuleBuild._
import sbtassembly.AssemblyKeys._

object BuildSettings {
  val buildOrganization = "eu.getintheloop"
  val buildVersion      = "0.0.9-SNAPSHOT"
  val buildScalaVersion = "2.11.7"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    liftVersion <<= liftVersion ?? "2.6",
    liftEdition <<= liftVersion apply { _.substring(0,3) },
    moduleName <<= (name, liftEdition) { (n, e) =>  n + "_" + e },
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions <<= scalaVersion map { v: String =>
      val opts = "-deprecation" :: "-unchecked" :: Nil
      if (v.startsWith("2.9.")) opts else opts ++ ( "-language:implicitConversions" :: "-language:postfixOps" :: Nil)},
    crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.4", "2.11.1", "2.11.7"),
    resolvers ++= Seq(
      "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public",
      "Shiro Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/",
      "sonatype.repo" at "https://oss.sonatype.org/content/repositories/public/"
    ),
    assemblyJarName in assembly := (moduleName.value + "_" + scalaVersion.value.substring(0,4) + "-" + version.value + ".jar"),
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

  liftVersion ?? "2.6"

  lazy val root = Project("lift-shiro-root", file("."),
    settings = BuildSettings.buildSettings ++ Seq(
      // the root is just an aggregator so dont publish a JAR
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Test, packageBin) := false,
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in (Compile, packageSrc) := false
    )) aggregate(library, example)


  lazy val library: Project = Project("lift-shiro", file("library"), 
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "org.apache.shiro" % "shiro-core" % "1.2.4",
        "org.apache.shiro" % "shiro-web" % "1.2.4",
        "commons-beanutils" % "commons-beanutils" % "1.8.3"
      )
    ) ++ Seq(
      libraryDependencies <+= liftVersion("net.liftweb" %% "lift-webkit" % _ % "provided")
    )
  )
  
  lazy val example = Project("lift-shiro-example", file("example"),
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "net.liftmodules"   %% "fobo-jquery_2.6"  % "1.3"               % "compile",
        "org.eclipse.jetty" % "jetty-webapp"      % "9.1.0.v20131115"   % "container",
        "org.eclipse.jetty" % "jetty-plus"        % "9.1.0.v20131115"   % "container",
        "javax.servlet"     % "javax.servlet-api" % "3.0.1"             % "provided",
        "ch.qos.logback"    % "logback-classic"   % "0.9.26"
      )
    ) ++ Seq(
      libraryDependencies <+= liftVersion("net.liftweb" %% "lift-webkit" % _ % "compile")
    ) ++ com.earldouglas.xsbtwebplugin.WebPlugin.webSettings
  ) dependsOn library
}
