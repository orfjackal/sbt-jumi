version := "1-SNAPSHOT"

libraryDependencies += "org.specsy" %% "specsy-scala" % "2.1.0" % "test"

jumiSettings

jumiJvmOptions := Seq("-ea", "-Xmx512M")

jumiIncludedTestsPattern := "glob:com/example/**{Test,Spec}.class"

//jumiDebugModeEnabled := true
