package edu.luc.cs.comp413.scala.monstergame

import org.junit.runner.RunWith
import org.robolectric.{Robolectric, RobolectricTestRunner}
import org.scalatest.junit.JUnitSuite

/**
 * Concrete Robolectric test subclass.
 */
@RunWith(classOf[RobolectricTestRunner])
class RobolectricFunctionalTest extends JUnitSuite with AbstractFunctionalTest {

  override protected lazy val activity =
    Robolectric.buildActivity(classOf[MainActivity]).create().start().get
}
