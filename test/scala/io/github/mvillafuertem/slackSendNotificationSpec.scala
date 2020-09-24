package io.github.mvillafuertem

import com.lesfurets.jenkins.unit.BasePipelineTest
import groovy.lang.Script
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
final class slackSendNotificationSpec extends AnyFlatSpecLike
  with Matchers
  with BeforeAndAfterAll {

  var basePipelineTest: BasePipelineTest = _

  override protected def beforeAll(): Unit = {
    basePipelineTest = new BasePipelineTest{}
    //val strings: Array[String] = helper.getScriptRoots.to(LazyList).concat(LazyList("vars")).toArray[String]
    basePipelineTest.getHelper.setScriptRoots(Array("vars/"):_*)
    basePipelineTest.setUp()
    super.beforeAll()
  }

  behavior of s"${getClass.getSimpleName}"

  ignore should "call" in {

    // g i v e n
    val name: String = "Pepe"
    val script: Script = basePipelineTest.getHelper.loadScript("vars/slackSendNotification.groovy")

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
