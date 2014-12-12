package edu.luc.cs.comp413.scala.monstergame
package controller

import android.view.{MotionEvent, View}
import edu.luc.cs.comp413.scala.monstergame.model.MonsterGame
import edu.luc.cs.comp413.scala.monstergame.view.MonsterView

/**
 * This class is responsible to mapping each tap on the screen to a call to the MonsterGame.killMonster.
 *
 * @param monsterGame the model underlying this controller
 * @param monsterView the view, in this context, responsible for mapping a tuple of (x, y) coordinates
 *                     into a specific cell in the model
 */
class TrackingTouchListener(monsterGame: MonsterGame, monsterView: MonsterView) extends View.OnTouchListener {

  override def onTouch(v: View, evt: MotionEvent): Boolean = {
    if (monsterGame.isRunning) {
      val action = evt.getAction
      action & MotionEvent.ACTION_MASK match {
        case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_POINTER_DOWN =>
          val idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
            MotionEvent.ACTION_POINTER_INDEX_SHIFT
          val pointerIdx = evt.findPointerIndex(evt.getPointerId(idx))
          removeMonster(monsterGame, evt.getX(pointerIdx), evt.getY(pointerIdx))
        case _ => false
      }
    }
    true
  }

  /**
   * Removes the monster in the specific cell that matches the (x, y) coordinates
   *  only if (1) a monster exists in that cell and (2) if that monster is in its
   *  vulnerable state
   *
   * @param monsterGame the game model
   * @param x the x coordinate of the screen
   * @param y the y coordinate of the screen
   */
  private def removeMonster(monsterGame: MonsterGame, x: Float, y: Float) = {
    val tuple = monsterView.getRowAndColFromCoordinates(x, y)
    monsterGame.killMonster(tuple._1, tuple._2)
  }
}
