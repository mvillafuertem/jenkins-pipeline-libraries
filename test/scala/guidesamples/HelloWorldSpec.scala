package guidesamples

import com.lesfurets.jenkins.unit.{BasePipelineTest, PipelineTestHelper}
import groovy.lang.Script
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

import scala.jdk.CollectionConverters._

@RunWith(classOf[JUnitRunner])
final class HelloWorldSpec extends AnyFlatSpecLike
  with Matchers
  with BeforeAndAfterAll {

  var basePipelineTest: BasePipelineTest = _

  override protected def beforeAll(): Unit = {
    basePipelineTest = new BasePipelineTest{}
    //val strings: Array[String] = helper.getScriptRoots.to(LazyList).concat(LazyList("vars")).toArray[String]
    basePipelineTest.getHelper.setScriptRoots(Array("src/main/jenkins", "vars/", "./."):_*)
    basePipelineTest.setUp()
    super.beforeAll()
  }

  behavior of "Hello World Spec"

  it should "say hello" in {

    val name: String = "Pepe"
    val script: Script = basePipelineTest.getHelper.loadScript("vars/sayHello.groovy")

    val methodName = "call"
    script.invokeMethod(methodName, name)

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

  //  // g i v e n
  //  val name: String = "Pepe"
  //  val script: Script = loadScript("vars/sayHello.groovy")
  //
  //
  //  // w h e n
  //  val methodName = "call"
  //  script.invokeMethod(methodName, name)
  //
  //
  //  // t h e n
  //  printCallStack()
  //
  //  getHelper.getCallStack
  //    .stream()
  //    .forEach { actual =>
  //      actual.getMethodName match {
  //        case "call" => actual.argsToString() shouldBe name
  //        case "sh" => actual.argsToString() should contain atMostOneOf("{script=ls, returnStdout=true}", "{script=ls -a, returnStdout=true}")
  //      }
  //    }
  //  assertJobStatusSuccess()

}

