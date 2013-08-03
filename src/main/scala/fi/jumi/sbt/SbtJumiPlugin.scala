// Copyright © 2013, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.sbt

import sbt._
import sbt.Keys._
import fi.jumi.launcher.JumiBootstrap
import fi.jumi.core.config._
import java.nio.file.{Paths, Path}
import scala.collection.JavaConversions._

object SbtJumiPlugin extends Plugin {

  val jumiTest = TaskKey[Unit]("jumi-test", "Run tests using Jumi")

  val jumiSuite = taskKey[SuiteConfigurationBuilder]("Configures the suite")
  val jumiClasspath = taskKey[Seq[Path]]("Classpath for running tests")
  val jumiJvmOptions = settingKey[Seq[String]]("JVM options for running tests")
  val jumiWorkingDirectory = settingKey[Path]("Working directory for running tests")
  val jumiIncludedTestsPattern = settingKey[String]("Test files to run. Same syntax as in java.nio.file.FileSystem#getPathMatcher")
  val jumiExcludedTestsPattern = settingKey[String]("Test files to not run. Same syntax as in java.nio.file.FileSystem#getPathMatcher")

  val jumiDaemon = taskKey[DaemonConfigurationBuilder]("Configures the daemon")
  val jumiHome = settingKey[Path]("Path to Jumi's home directory")
  val jumiTestThreadsCount = settingKey[Int]("Number of test threads")
  val jumiStartupTimeout = settingKey[Long]("Timeout after which to shutdown the daemon process")
  val jumiIdleTimeout = settingKey[Long]("Timeout after which to shutdown the daemon process")
  val jumiDebugModeEnabled = settingKey[Boolean]("Whether to show full debug information")

  override val settings = Seq(
    jumiTest <<= (jumiSuite, jumiDaemon, jumiDebugModeEnabled) map jumiTestTask dependsOn (compile in Test),

    jumiSuite := new SuiteConfigurationBuilder().
      setClassPath(jumiClasspath.value: _*).
      setJvmOptions(jumiJvmOptions.value: _*).
      setWorkingDirectory(jumiWorkingDirectory.value).
      setIncludedTestsPattern(jumiIncludedTestsPattern.value).
      setExcludedTestsPattern(jumiExcludedTestsPattern.value),
    jumiClasspath := (fullClasspath in Test).value map (_.data.toPath),
    jumiJvmOptions := SuiteConfiguration.DEFAULTS.getJvmOptions,
    jumiWorkingDirectory := Paths.get(SuiteConfiguration.DEFAULTS.getWorkingDirectory),
    jumiIncludedTestsPattern := SuiteConfiguration.DEFAULTS.getIncludedTestsPattern,
    jumiExcludedTestsPattern := SuiteConfiguration.DEFAULTS.getExcludedTestsPattern,

    jumiDaemon := new DaemonConfigurationBuilder().
      setJumiHome(jumiHome.value).
      setTestThreadsCount(jumiTestThreadsCount.value).
      setStartupTimeout(jumiStartupTimeout.value).
      setIdleTimeout(jumiIdleTimeout.value),
    jumiHome := DaemonConfiguration.DEFAULTS.getJumiHome,
    jumiTestThreadsCount := DaemonConfiguration.DEFAULTS.getTestThreadsCount,
    jumiStartupTimeout := DaemonConfiguration.DEFAULTS.getStartupTimeout,
    jumiIdleTimeout := DaemonConfiguration.DEFAULTS.getIdleTimeout,
    jumiDebugModeEnabled := false
  )

  private def jumiTestTask(suite: SuiteConfigurationBuilder, daemon: DaemonConfigurationBuilder, debugMode: Boolean) {
    try {
      val bootstrap = new JumiBootstrap()
      bootstrap.suite = suite
      bootstrap.daemon = daemon
      if (debugMode) {
        bootstrap.enableDebugMode()
      }
      bootstrap.runSuite()
    } catch {
      case e: AssertionError => throw new TestsFailedException
    }
  }
}
