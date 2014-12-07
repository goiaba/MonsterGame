package edu.luc.etl.cs313.scala.uidemo.model

import java.util.{TimerTask, Timer}
import java.util.concurrent.{Executors, Future, ExecutorService}

import android.os.Handler

import scala.util.Random

/**
 * Created by bruno on 06/12/14.
 */
case class Monster(private var cell: Cell, vulnerableTime: Long) extends Runnable {

  private val maxDelay = 1500

  private var monsterChangeListener: MonsterGame.MonsterChangeListener = _

  private var liveThread: ExecutorService = _

  private var workThread: ExecutorService = _

  private var task: Future[_] = null

  private var vulnerable = Random.nextBoolean()

  private val handler: Handler = new Handler()

  private var timer: Timer = new Timer()

  timer.schedule(new TimerTask {
    override def run(): Unit = handler.post(new Runnable {
      override def run(): Unit = changeState()
    })
  }, 500, vulnerableTime)

  /** @param l set the change listener. */
  def setMonsterChangeListener(l: MonsterGame.MonsterChangeListener) = monsterChangeListener = l

  def setCell(cell: Cell): Unit = this.synchronized { this.cell = cell }

  def getCell(): Cell = this.synchronized { cell }

  def isVulnerable: Boolean = vulnerable

  protected def isAlive: Boolean = liveThread != null

  private def getRandomDelayToMove: Long = {
    Random.nextInt(maxDelay).toLong
  }

  def start(): Unit = {
    this.synchronized {
      if (!isAlive) {
          liveThread = Executors.newFixedThreadPool(1)
          workThread = Executors.newFixedThreadPool(1)
      }
        liveThread.execute(this)
    }
  }

  def kill(): Unit = {
    this.synchronized {
      if (isAlive) {
        liveThread.shutdown()
        workThread.shutdown()
        liveThread = null
        workThread = null
      }
      timer.cancel()
      timer = null
    }
  }

  def changeState(): Unit = {
    this.synchronized {
      vulnerable = !vulnerable
    }
    notifyListener()
  }

  def move(): Unit = {
    this.synchronized {
      if (isAlive) cell.moveToRandomAdjacentCell()
    }
    notifyListener()
  }

  def die(): Unit = {
    synchronized {
      if (isVulnerable) {
        cell = null
        kill()
      }
    }
  }

  protected def execute(runnable: Runnable): Unit = {
    this.synchronized {
      if (task != null && !task.isDone()) {
        task.cancel(true)
      }
      if (isAlive)
        task = workThread.submit(runnable)
    }
  }

  override def run(): Unit = {
    while (!Thread.interrupted()) {
      try {
        Thread.sleep(getRandomDelayToMove)
        execute(moveRunnable)
      } catch {
        case _: InterruptedException => Thread.currentThread().interrupt()
      }
    }
  }

  private def moveRunnable: Runnable = new Runnable {
    override def run(): Unit =
    try {
      handler.post(new Runnable {
        override def run(): Unit = move()
        })
      } catch {
        case _: InterruptedException => Thread.currentThread().interrupt()
      }
  }

  private def changeStateRunnable: Runnable = new Runnable {
    override def run(): Unit =
      try {
        handler.post(new Runnable {
          override def run(): Unit = changeState()
        })
      } catch {
        case _: InterruptedException => Thread.currentThread().interrupt()
      }
  }

  private def notifyListener(): Unit =
    if (null != monsterChangeListener)
      monsterChangeListener.onMonsterChange(this)

}
