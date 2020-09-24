package io.github.mvillafuertem

import java.beans.Introspector

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

trait BasePipelineSpec extends AnyFlatSpecLike
  with Matchers
  with BeforeAndAfterEach {

  var basePipelineTest: BasePipelineTest = _

  val stepName: String = Introspector.decapitalize(
    getClass
      .getSimpleName
      .replaceAll("Spec", ""
      )
  )

  val scriptLocation: String = s"vars/$stepName.groovy"

  override protected def beforeEach(): Unit = {
    basePipelineTest = new BasePipelineTest {}
    basePipelineTest.getHelper.setScriptRoots(Array("vars/"): _*)
    basePipelineTest.setUp()
    super.beforeEach()
  }

}
