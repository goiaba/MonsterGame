package edu.luc.etl.cs313.scala.uidemo

import org.junit.Assert._
import org.junit.Test

/**
 * An abstract GUI-based functional test for the hello app.
 * This follows the XUnit Testcase Superclass pattern.
 */
trait AbstractFunctionalTest {

  /**
   * The activity to be provided by concrete subclasses of this test.
   */
  protected def activity(): MainActivity

  @Test def testActivityExists() {
    assertNotNull(activity)
  }

  // TODO test short and long click and resulting toasts
}
