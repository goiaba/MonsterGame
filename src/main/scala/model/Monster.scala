package edu.luc.cs.comp413.scala.monstergame.model

import java.util.concurrent._

import scala.util.Random

/**
 * Created by bruno on 06/12/14.
 */

/**
 * This class represents a monster with specific vulnerability time and with random
 *  movements that occurs periodically
 *
 * @param vulnerableInterval the period of time in which the monster is in a vulnerable state
 * @param moveInterval the interval between each movement
 * @param monsterGameThreadPool a pool of threads to execute the tasks responsible for moving
 *                               and changing the state of this  monster
 */
abstract class Monster(private val vulnerableInterval: Long,
                       private val moveInterval: Long,
                       private var monsterGameThreadPool: ScheduledThreadPoolExecutor) {

  private var moveTask: ScheduledFuture[_] = _

  private var changeStateTask: ScheduledFuture[_] = _

  private var vulnerable = Random.nextBoolean()

  private var cell: Cell = null

  { addMonsterTasksToTheThreadPool() }

  def addMonsterTasksToTheThreadPool(): Unit = {
    moveTask = monsterGameThreadPool
      .scheduleWithFixedDelay(moveRunnable,
        (Random.nextFloat() * 4 * moveInterval).toLong,
        moveInterval,
        TimeUnit.MILLISECONDS)
    changeStateTask = monsterGameThreadPool
      .scheduleWithFixedDelay(changeStateRunnable,
        (Random.nextFloat() * 4 * vulnerableInterval).toLong,
        vulnerableInterval,
        TimeUnit.MILLISECONDS)
  }

  /**
   *
   * @param monsterTask The task that need to be ran
   *                     into an specific thread
   */
  def runOnSpecificThread(monsterTask: => Unit): Unit

  def setCell(cell: Cell): Unit = this.cell = cell

  def getCell: Cell = cell

  def isVulnerable: Boolean = vulnerable

  def isAlive: Boolean = cell != null

  def changeState(): Unit = {
    if (isAlive) vulnerable = !vulnerable
  }

  def move(): Unit = {
    if (isAlive) {
      cell.moveToRandomAdjacentCell()
    }
  }

  def die(): Unit = {
    if (isVulnerable) {
      moveTask.cancel(true)
      changeStateTask.cancel(true)
      monsterGameThreadPool = null
      cell = null
    }
  }

  /**
   * Task responsible for moving this monster
   *
   * @return a runnable that represents the task and that will be
   *          added to the monsterGameThreadPool to be executed
   */
  private def moveRunnable: Runnable = new Runnable {
    override def run(): Unit =
      try {
        runOnSpecificThread(move())
      } catch {
        case _: InterruptedException => Thread.currentThread().interrupt()
      }
  }

  /**
   * Task responsible for changing this monster state
   *
   * @return a runnable that represents the task and that will be
   *          added to the monsterGameThreadPool to be executed
   */
  private def changeStateRunnable: Runnable = new Runnable {
    override def run(): Unit =
      try {
        runOnSpecificThread(changeState())
      } catch {
        case _: InterruptedException => Thread.currentThread().interrupt()
      }
  }

}
