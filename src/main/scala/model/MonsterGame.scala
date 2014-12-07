package edu.luc.etl.cs313.scala.uidemo.model

import edu.luc.etl.cs313.scala.uidemo.model.MonsterGame.MonsterChangeListener

import scala.collection.mutable.ListBuffer

/**
 * Created by bruno on 04/12/14.
 */

trait MonsterGameLevel {
  val levelDesc: String
  val numberOfMonsters: Int
  val monsterVulnerableSliceTime: Int
}

object Level1 extends MonsterGameLevel {
  override val levelDesc = "Easy"
  override val numberOfMonsters = 12
  override val monsterVulnerableSliceTime = 500
}

object Level2 extends MonsterGameLevel {
  override val levelDesc = "Moderate"
  override val numberOfMonsters = 16
  override val monsterVulnerableSliceTime = 250
}

object Level3 extends MonsterGameLevel {
  override val levelDesc = "Hard"
  override val numberOfMonsters = 20
  override val monsterVulnerableSliceTime = 125
}

object MonsterGame {
  trait MonsterGameChangeListener {
    /** @param monsterGame the monsters that changed. */
    def onMonsterGameChange(monsterGame: MonsterGame): Unit
  }
  trait MonsterChangeListener {
    def onMonsterChange(monster: Monster): Unit
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

  def getElapsedTime = { elapsedTime }

  def setLevel(level: MonsterGameLevel) = this.level = level

  def getLevel() = this.level
  
  def getAvailableLevels: List[MonsterGameLevel] = {
    List[MonsterGameLevel](Level1, Level2, Level3)
  }

  def isGameEnded: Boolean = {
    monsters.isEmpty
  }

  def startGame: Unit = {
    for (m <- 1 to level.numberOfMonsters)
      createMonster()
    startTime = System.currentTimeMillis()
  }
  
  def endGame: Unit = {
    elapsedTime = System.currentTimeMillis() - startTime
  }

  def getLastMonster = if (monsters.size <= 0) null else monsters.last

  /** @param l set the change listener. */
  def setMonsterGameChangeListener(l: MonsterGame.MonsterGameChangeListener) = monsterGameChangeListener = l

  def getMonsters(): List[Monster] = monsters.toList

  def createMonster(): Unit = {
    this.synchronized {
      val newMonster = gameBoard.putMonsterInARandomCell(level.monsterVulnerableSliceTime)
      if (null != newMonster) {
        newMonster.setMonsterChangeListener(new MonsterChangeListener {
          override def onMonsterChange(monster: Monster): Unit = notifyListener()
        })
        monsters += newMonster
        notifyListener()
        newMonster.start()
      }
    }
  }

  def killMonster(row: Int, col: Int): Unit = {
    this.synchronized {
      val monster = gameBoard.getMonsterFromCell(row, col)
      if (null != monster && monster.isVulnerable) {
        gameBoard.removeMonsterFromTheCell(row, col)
        monsters -= monster
        monster.die()
        if (monsters.isEmpty)
          endGame
        notifyListener()
      }
    }
  }

  private def notifyListener(): Unit =
    if (null != monsterGameChangeListener)
      monsterGameChangeListener.onMonsterGameChange(this)

}
