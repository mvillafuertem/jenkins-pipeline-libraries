package io.github.mvillafuertem.aws

import groovy.lang.Script

import scala.jdk.CollectionConverters._

// This doesn't work with shared libraries
// Because it is not a groovy class
// You get an org.jenkinsci.plugins.workflow.cps.CpsCompilationErrorsException
// It is only to toy with scala-test
// You need create a jar or jenkins plugin to use scala in jenkins pipeline
// If is a jar use http://docs.groovy-lang.org/latest/html/documentation/grape.html#_quick_start
final class AwsWorkflow(script: Script) extends Serializable {

  def call(): Unit = {
    script.invokeMethod("sh", Map[String, Any]("script" -> "ls -la").asJava)
  }

}
