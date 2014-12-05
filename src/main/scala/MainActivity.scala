package edu.luc.etl.cs313.scala.uidemo

import android.app.Activity
import android.os.{AsyncTask, Bundle}
import edu.luc.etl.cs313.scala.uidemo.controller._
import edu.luc.etl.cs313.scala.uidemo.model._

/** Main activity for Android UI demo program. Responsible for Android lifecycle. */
class MainActivity extends Activity with TypedActivity with Controller {

  //TODO Get the number of monsters from view (level).
  /** The application model */
  override val monsterGameModel = new MonsterGame(10, 8)
  /** The monster generator */
//  var monsterGenerator: MonsterGenerator = _

  override def onCreate(state: Bundle) = {
    super.onCreate(state)
    setContentView(R.layout.main)
    connectMonsterGameView()
  }

  override def onStart() = {
    super.onStart()
      monsterGameModel.startGame
//    monsterGenerator = new MonsterGenerator(monsterGameModel, this)
//    monsterGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
  }

  override def onStop() = {
//    monsterGenerator.cancel(true)
//    monsterGenerator = null
    super.onStop()
  }
}