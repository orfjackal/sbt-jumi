resolvers += Resolver.typesafeRepo("snapshots")

libraryDependencies <+= (sbtVersion) {
  sv => "org.scala-sbt" % "scripted-plugin" % sv
}

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8")
