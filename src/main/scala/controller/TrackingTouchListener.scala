package edu.luc.etl.cs313.scala.uidemo
package controller

import android.view.{MotionEvent, View}
import edu.luc.etl.cs313.scala.uidemo.model.MonsterGame
import edu.luc.etl.cs313.scala.uidemo.view.MonsterView

import scala.collection.mutable.ArrayBuffer

/** Listen for taps. */
class TrackingTouchListener(monsterGame: MonsterGame, monsterView: MonsterView) extends View.OnTouchListener {

  var pointerIdx = 0

  override def onTouch(v: View, evt: MotionEvent): Boolean = {
    val action = evt.getAction
    action & MotionEvent.ACTION_MASK match {
      case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_POINTER_DOWN =>
        val idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
          MotionEvent.ACTION_POINTER_INDEX_SHIFT
          val pointerIdx = evt.findPointerIndex(evt.getPointerId(idx))
          removeMonster(monsterGame, evt.getX(pointerIdx), evt.getY(pointerIdx))
      case _ => false
    }
    true
  }

  private def removeMonster(monsterGame: MonsterGame, x: Float, y: Float) = {
    val tuple = monsterView.getRowAndColFromCoordinates(x, y)
    monsterGame.killMonster(tuple._1, tuple._2)
  }
}
