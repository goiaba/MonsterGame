package edu.luc.cs.comp413.scala.monstergame
package controller

import android.app.Activity
import android.view.{Menu, MenuItem}
import edu.luc.cs.comp413.scala.monstergame.common.MonsterGameMemento
import edu.luc.cs.comp413.scala.monstergame.model.MonsterGame.MonsterGameChangeListener
import edu.luc.cs.comp413.scala.monstergame.model.{Level1, Level2, Level3, MonsterGame}
import edu.luc.cs.comp413.scala.monstergame.view.MonsterView

/** Controller mixin (stackable trait) for Android UI demo program */
trait Controller extends Activity with TypedActivityHolder {

  protected var monsterGameModel: MonsterGame = _

  private var monsterView: MonsterView = _

  private var startItemMenuEnabled = true

  /**
   * Responsible for (1) instantiate the view, (2) instantiate the model passing the number
   *  of rows and columns defined by the view, (3) link the view with the model to permit
   *  view updates, (4) link the view with the controller that handles screen taps
   * (TrackingTouchListener) and (5) set the change listener.
   */
  def initGame(memento: MonsterGameMemento): Unit = {
    monsterView = findView(TR.monsters)
    monsterGameModel = new MonsterGame(monsterView.getNumberOfRows, monsterView.getNumberOfCols)
    monsterView.setMonsterGame(monsterGameModel)
    monsterView.setOnTouchListener(new TrackingTouchListener(monsterGameModel, monsterView))
    monsterGameModel.setMonsterGameChangeListener(new MonsterGameChangeListener {
      /** @param monsterGame the monsters that changed. */
      override def onMonsterGameChange(monsterGame: MonsterGame): Unit = {
        if (!monsterGame.isRunning && !monsterGame.isCancelled) {
          monsterView.showAlertDialog(monsterGame.getElapsedTime)
          startItemMenuEnabled = true
        }
        monsterView.invalidate()
      }
      override def runOnSpecificThread(monsterTask: => Unit): Unit = runOnUiThread(new Runnable() {
        override def run(): Unit = { monsterTask; monsterView.invalidate() }
      })
    })
    if (null != memento) {
      monsterGameModel.restoreFromMemento(memento)
      startItemMenuEnabled = false
    }
  }

  override def onCreateOptionsMenu(menu: Menu) = {
    getMenuInflater.inflate(R.menu.simple_menu, menu)
    true
  }

  override def onPrepareOptionsMenu (menu: Menu): Boolean = {
    super.onPrepareOptionsMenu(menu)
    menu.findItem(R.id.start_game).setEnabled(startItemMenuEnabled)
    menu.findItem(R.id.cancel_game).setEnabled(!startItemMenuEnabled)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      /**
       * - The three first options set the game level
       * - The fourth one starts a new game and is available only when
       *    there is no game running
       * - The fifth one ends a running game and is available only when
       *    there is a game running
       */
      case R.id.menu_level1 => monsterGameModel.setLevel(Level1); true
      case R.id.menu_level2 => monsterGameModel.setLevel(Level2); true
      case R.id.menu_level3 => monsterGameModel.setLevel(Level3); true
      case R.id.start_game => {
        monsterGameModel.startGame
        startItemMenuEnabled = false
        true
      }
      case R.id.cancel_game =>
        monsterGameModel.cancelGame
        startItemMenuEnabled = true
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }
}
