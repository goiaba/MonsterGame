package edu.luc.cs.comp413.scala.monstergame

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import edu.luc.cs.comp413.scala.monstergame.common.MonsterGameMemento
import edu.luc.cs.comp413.scala.monstergame.controller._

/** Main activity for Android UI demo program. Responsible for Android lifecycle. */
class MainActivity extends Activity with TypedActivity with Controller {

  private val mementoKey = "mementoKey"
  private var memento: MonsterGameMemento  = null

  override def onRestoreInstanceState(savedInstanceState: Bundle): Unit = {
    super.onRestoreInstanceState(savedInstanceState)
    Log.i("MainActivity", "onRestoreInstanceState")
    memento = savedInstanceState.get(mementoKey)
      .asInstanceOf[MonsterGameMemento]
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    Log.i("MainActivity", "onSaveInstanteState")
    if (monsterGameModel.isRunning)
      outState.putSerializable(mementoKey, monsterGameModel.getMemento)
    super.onSaveInstanceState(outState)
  }

  override def onStart(): Unit = {
    super.onStart()
    Log.i("MainActivity", "onStart")
  }

  override def onCreate(state: Bundle) = {
    super.onCreate(state)
    setContentView(R.layout.main)
    Log.i("MainActivity", "onCreate")
    /**
     * This code permits we init the model only after the layout of the screen
     *  has finished. Otherwise the computation of the model matrix dimensions
     *  does not occur properly
     */
    val layout: LinearLayout = findViewById(R.id.root).asInstanceOf[LinearLayout]
    val observer: ViewTreeObserver = layout.getViewTreeObserver
    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener {
      override def onGlobalLayout(): Unit = {
        layout.getViewTreeObserver.removeOnGlobalLayoutListener(this)
        initGame(memento)
      }
    })
  }

  override def onStop() = {
    monsterGameModel.cancelGame()
    super.onStop()
  }
}