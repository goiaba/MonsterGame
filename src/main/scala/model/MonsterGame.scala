package edu.luc.cs.comp413.scala.monstergame.model

import java.util.concurrent.{Executors, ScheduledThreadPoolExecutor}

import android.util.Log
import edu.luc.cs.comp413.scala.monstergame.common.MonsterGameMemento

import scala.collection.mutable.ListBuffer

/**
 * Created by bruno on 04/12/14.
 */

trait MonsterGameLevel {
  val levelDesc: String
  val numberOfMonsters: Int
  val monsterVulnerableSliceTime: Int
  val maxDelayToMove: Int
}

object Level1 extends MonsterGameLevel {
  override val levelDesc = "Easy"
  override val numberOfMonsters = 12
  override val monsterVulnerableSliceTime = 500
  override val maxDelayToMove = 1500
}

object Level2 extends MonsterGameLevel {
  override val levelDesc = "Moderate"
  override val numberOfMonsters = 16
  override val monsterVulnerableSliceTime = 250
  override val maxDelayToMove = 1500
}

object Level3 extends MonsterGameLevel {
  override val levelDesc = "Hard"
  override val numberOfMonsters = 20
  override val monsterVulnerableSliceTime = 125
  override val maxDelayToMove = 1500
}

object MonsterGame {
  trait MonsterGameChangeListener {
    /** @param monsterGame the monsters that changed. */
    def onMonsterGameChange(monsterGame: MonsterGame): Unit
    def runOnSpecificThread(f: => Unit): Unit
  }
}

/**
 * This class controls the game.
 *
 * @param rows number of rows of the game board
 * @param cols number of columns of the game board
 */
class MonsterGame(val rows: Int, val cols: Int) {

  private var monsterGameChangeListener: MonsterGame.MonsterGameChangeListener = _

  /**
   * Number of threads in the monsterGameThreadPool
   */
  private val poolSize = 2

  /**
   * A pool of threads that are responsible for execute the moving and changing of
   *  state of each alive monster.
   */
  private val monsterGameThreadPool: ScheduledThreadPoolExecutor =
    Executors.newScheduledThreadPool(poolSize).asInstanceOf[ScheduledThreadPoolExecutor]

  /**
   * Level of game defaults to easiest level. The level influences
   *  the initial number of monsters and the interval they are
   *  in the vulnerable state
   */
  private var level: MonsterGameLevel = Level1

  /**
   * A representation of the game board
   */
  private val gameBoard: Field = new MonsterField(rows, cols)

  /**
   * A list of the monsters existent in this game board
   */
  private val monsters = new ListBuffer[Monster]

  private var startTime: Long = 0

  private var elapsedTime: Long = 0

  private var cancelled: Boolean = false

  /**
   * Store the current state of the game
   *
   * @return a memento of this MonsterGame
   */
  def getMemento: MonsterGameMemento = {
    new MonsterGameMemento {
      override val level: MonsterGameLevel = MonsterGame.this.level
      override val numberOfAliveMonsters: Int = MonsterGame.this.monsters.size
      override val elapsedTime: Long = System.currentTimeMillis() - MonsterGame.this.startTime
    }
  }

  /**
   * Restore the state of the game from a previously
   *  stored memento
   *
   * @param memento state information to restore the game
   */
  def restoreFromMemento(memento: MonsterGameMemento): Unit = {
    MonsterGame.this.level = memento.level
    MonsterGame.this.startTime = System.currentTimeMillis() - memento.elapsedTime
    MonsterGame.this.startGame(memento.numberOfAliveMonsters)
  }

  def getElapsedTime = elapsedTime

  def isCancelled = cancelled

  def setLevel(level: MonsterGameLevel) = this.level = level

  def getLevel: MonsterGameLevel = this.level
  
  def getAvailableLevels: List[MonsterGameLevel] = List[MonsterGameLevel](Level1, Level2, Level3)

  def isRunning: Boolean = monsters.nonEmpty

  def startGame(): Unit = synchronized {
    startGame(level.numberOfMonsters)
    startTime = System.currentTimeMillis()
  }

  def calcElapsedTime(): Unit = elapsedTime = System.currentTimeMillis() - startTime

  def cancelGame(): Unit = synchronized {
    cancelled = true
    monsters.foreach(monster => monster.release())
    monsters.foreach(monster => Log.i("MonsterGame", if (null!=monster.getCell) monster.getCell.toString else "null cell"))
    monsters.foreach(monster => Log.i("MonsterGame", if (null!=monster.getCell && null!=monster.getCell.getMonster) monster.getCell.getMonster.toString else "null monster in cell"))
    monsters.clear()
    gameBoard.reset()
    notifyListener()
  }

  /** @param l set the change listener. */
  def setMonsterGameChangeListener(l: MonsterGame.MonsterGameChangeListener) =
    monsterGameChangeListener = l

  def getMonsters: List[Monster] = monsters.toList

  def createMonster(): Unit = synchronized {
    val newMonster = new Monster(level.monsterVulnerableSliceTime,
          level.maxDelayToMove, monsterGameThreadPool) {
            override def runOnSpecificThread(monsterTask: => Unit): Unit =
              monsterGameChangeListener.runOnSpecificThread(monsterTask)
    }
    if (gameBoard.putMonsterInARandomCell(newMonster)) {
      monsters += newMonster
      notifyListener()
    }
  }

  def killMonster(row: Int, col: Int): Unit = synchronized {
    val monster = gameBoard.getMonsterFromCell(row, col)
    if (null != monster && monster.isVulnerable) {
      gameBoard.releaseCell(monster.getCell)
      monster.die()
      monsters -= monster
      if (monsters.isEmpty)
        calcElapsedTime()
      notifyListener()
    }
  }

  private def startGame(numberOfMonsters: Int): Unit = {
    cancelled = false
    for (m <- 1 to numberOfMonsters)
      createMonster()
    notifyListener()
  }

  private def notifyListener(): Unit =
    if (null != monsterGameChangeListener)
      monsterGameChangeListener.onMonsterGameChange(this)

}
