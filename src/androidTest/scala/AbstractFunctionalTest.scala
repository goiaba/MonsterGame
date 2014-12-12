package edu.luc.cs.comp413.scala.monstergame

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
