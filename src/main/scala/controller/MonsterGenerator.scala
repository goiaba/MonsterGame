package edu.luc.etl.cs313.scala.uidemo
package controller

import android.os.AsyncTask
import android.util.Log
import edu.luc.etl.cs313.scala.uidemo.model.MonsterGame

// TODO figure out how to replace this with a future

/** Generate new monsters, one per second. */
class MonsterGenerator(monsterGame: MonsterGame, controller: Controller)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  /** Delay between generation of monsters. */
  val DELAY = 1000 // TODO externalize

  override protected def onProgressUpdate(values: AnyRef*) =
    controller.createMonster(monsterGame) // this method runs on the UI thread!

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "monster generator scheduling monster creation")
      publishProgress(null)
      try { Thread.sleep(DELAY) } catch { case _: InterruptedException => return null }
    }
    null
  }
}
