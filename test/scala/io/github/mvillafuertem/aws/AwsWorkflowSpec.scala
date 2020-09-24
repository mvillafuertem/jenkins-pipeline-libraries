package io.github.mvillafuertem.aws

import groovy.lang.Script
import io.github.mvillafuertem.BasePipelineSpec
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
final class AwsWorkflowSpec extends BasePipelineSpec {

  behavior of s"${getClass.getSimpleName}"

  it should "call" in {

    // g i v e n
    val script: Script = basePipelineTest.getHelper.loadScript(scriptLocation)

    // w h e n
    val _: Unit = new AwsWorkflow(script).call()


    // t h e n
    basePipelineTest.getHelper.getCallStack
      .stream()
      .forEach { actual =>
        actual.getMethodName match {
          case "sh" => actual.argsToString() shouldBe "[script:ls -la]"
        }
      }
    basePipelineTest.printCallStack()
    basePipelineTest.assertJobStatusSuccess()

  }


}
