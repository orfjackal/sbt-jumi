addSbtPlugin("fi.jumi.sbt" % "sbt-jumi" % sys.props("sbt-jumi.version"))

resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL+"/.m2/repository"
