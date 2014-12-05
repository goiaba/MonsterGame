package edu.luc.etl.cs313.scala.uidemo
package controller

import android.app.Activity
import edu.luc.etl.cs313.scala.uidemo.model.MonsterGame.MonsterGameChangeListener
import edu.luc.etl.cs313.scala.uidemo.model.{Monster, MonsterGame}
import edu.luc.etl.cs313.scala.uidemo.view.MonsterView

/** Controller mixin (stackable trait) for Android UI demo program */
trait Controller extends Activity with TypedActivityHolder {

  val monsterGameModel: MonsterGame

  private var monsterView: MonsterView = _

  def getNumberOfCols = monsterView.getNumberOfCols
  def getNumberOfRows = monsterView.getNumberOfRows

  def createMonster(monsterGame: MonsterGame): Unit = {
    monsterGame.addMonster()
  }

//  def killMonster(monsterGame: MonsterGame, monster: Monster) = {
//
//  }
//
//  def moveMonster(monsterGame: MonsterGame, monster: Monster) = {
//
//  }

  def connectMonsterGameView(): Unit = {
    monsterView = findView(TR.monsters)

    monsterView.setMonsterGame(monsterGameModel)
    monsterView.setOnTouchListener(new TrackingTouchListener(monsterGameModel, monsterView))

    monsterGameModel.setMonsterGameChangeListener(new MonsterGameChangeListener {
      /** @param monsterGame the monsters that changed. */
      override def onMonsterGameChange(monsterGame: MonsterGame): Unit = {
        val m: Monster = monsterGame.getLastMonster
        findView(TR.text1).setText(if (null == m) "" else "Row: " + m.row.toString)
        findView(TR.text2).setText(if (null == m) "" else "Col: " + m.col.toString)
        monsterView.invalidate()
      }
    })
  }
}