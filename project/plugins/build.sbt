resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"    

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.1.1-"+v))

libraryDependencies <+= (sbtVersion) { sv =>
  "net.databinder" %% "posterous-sbt" % ("0.3.0_sbt" + sv)
}
