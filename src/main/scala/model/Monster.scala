package edu.luc.cs.comp413.scala.monstergame.model

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicBoolean

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

  private val moveTask: ScheduledFuture[_] = monsterGameThreadPool
    .scheduleWithFixedDelay(moveRunnable,
      (Random.nextFloat() * moveInterval).toLong,
      moveInterval,
      TimeUnit.MILLISECONDS)

  private val changeStateTask: ScheduledFuture[_] = monsterGameThreadPool
    .scheduleWithFixedDelay(changeStateRunnable,
      (Random.nextFloat() * vulnerableInterval).toLong,
      vulnerableInterval,
      TimeUnit.MILLISECONDS)

  private val vulnerable: AtomicBoolean = new AtomicBoolean(Random.nextBoolean())

  private var cell: Cell = null

  /**
   *
   * @param monsterTask The task that need to be ran
   *                     into an specific thread
   */
  def runOnSpecificThread(monsterTask: => Unit): Unit

  def getCell: Cell = synchronized { cell }

  def isAlive: Boolean = synchronized { cell != null }

  def isVulnerable: Boolean = vulnerable.get()

  /**
   * Change the state of this monster
   */
  def changeState(): Unit = {
    if (isAlive) vulnerable.set(!vulnerable.get())
  }

  /**
   * Move this monster in the field
   */
  def move(): Unit = {
    if (isAlive) {
      if (null == getCell.getMonster) getCell.setMonster(this)
      synchronized { getCell.moveToRandomAdjacentCell() }
    }
  }

  /**
   * This monster dies if this method is called when
   *  this monster is in vulnerable state
   */
  def die(): Unit = {
    if (isVulnerable) release()
  }

  /**
   * This method is responsible for removing this monster tasks of
   *  the thread pool and removing the reference to the cell which
   *  this monster was located
   */
  def release(): Unit = {
    moveTask.cancel(true)
    changeStateTask.cancel(true)
    monsterGameThreadPool = null
    removeRelWithCel()
  }

  def removeRelWithCel(): Unit = synchronized {
    getCell.removeMonster()
    setCell(null)
  }

  def createRelWithCel(cell: Cell): Unit = synchronized {
    setCell(cell)
    getCell.setMonster(this)
  }

  private def setCell(cell: Cell): Unit = this.synchronized { this.cell = cell }

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
