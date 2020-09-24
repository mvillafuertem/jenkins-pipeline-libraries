package io.github.mvillafuertem.aws

import groovy.lang.Script

import scala.jdk.CollectionConverters._


final class AwsWorkflow(script: Script) extends Serializable {

  def call(): Unit = {
    script.invokeMethod("sh", Map[String, Any]("script" -> "ls -la").asJava)
  }

}
