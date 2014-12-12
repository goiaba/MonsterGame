package edu.luc.cs.comp413.scala.monstergame

import edu.luc.cs.comp413.scala.monstergame.model.MonsterGameLevel

/** Shared constants. */
package object common {
  val TAG = "edu.luc.cs.comp413.scala.monstergame" // for logging

  /** A memento of the game. */
  trait MonsterGameMemento extends Serializable {
    val level: MonsterGameLevel
    val elapsedTime: Long
    val numberOfAliveMonsters: Int
  }
}
