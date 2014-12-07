package edu.luc.etl.cs313.scala.uidemo
package controller

import android.app.Activity
import edu.luc.etl.cs313.scala.uidemo.model.MonsterGame
import edu.luc.etl.cs313.scala.uidemo.model.MonsterGame.MonsterGameChangeListener
import edu.luc.etl.cs313.scala.uidemo.view.MonsterView

/** Controller mixin (stackable trait) for Android UI demo program */
trait Controller extends Activity with TypedActivityHolder {

  val monsterGameModel: MonsterGame

  private var monsterView: MonsterView = _

  def getNumberOfCols = monsterView.getNumberOfCols
  def getNumberOfRows = monsterView.getNumberOfRows

  def createMonster(monsterGame: MonsterGame): Unit = {
    monsterGame.createMonster()
  }

  def connectMonsterGameView(): Unit = {
    monsterView = findView(TR.monsters)

    monsterView.setMonsterGame(monsterGameModel)
    monsterView.setOnTouchListener(new TrackingTouchListener(monsterGameModel, monsterView))

    monsterGameModel.setMonsterGameChangeListener(new MonsterGameChangeListener {
      /** @param monsterGame the monsters that changed. */
      override def onMonsterGameChange(monsterGame: MonsterGame): Unit = {
        if (monsterGame.isGameEnded)
          monsterView.showAlertDialog(monsterGame.getElapsedTime)
        monsterView.invalidate()
      }
    })
  }
}