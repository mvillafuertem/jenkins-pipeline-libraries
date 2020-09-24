package io.github.mvillafuertem

import groovy.lang.Script
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
final class SlackSendNotificationSpec extends BasePipelineSpec {

  behavior of s"${getClass.getSimpleName}"

  ignore should "call" in {

    // g i v e n
    val name: String = "Pepe"
    val script: Script = basePipelineTest.getHelper.loadScript(scriptLocation)

    // w h e n
    val methodName = "call"
    script.invokeMethod(methodName, name)

    // t h e n
    basePipelineTest.getHelper.getCallStack
      .stream()
      .forEach { actual =>
        actual.getMethodName match {
          case "call" => actual.argsToString() shouldBe name
          case "sh" => actual.argsToString() should contain.atMostOneOf("{script=ls, returnStdout=true}", "{script=ls -a, returnStdout=true}")
        }
      }
    basePipelineTest.printCallStack()
    basePipelineTest.assertJobStatusSuccess()

  }

}
