package edu.luc.cs.comp413.scala.monstergame.model

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by bruno on 04/12/14.
 */
/**
 * This trait represents the basic component of a matrix: a cell.
 */
trait Cell {

  /**
   * @return true if the cell is empty, false otherwise
   */
  def isEmpty: Boolean

  /**
   * @return the row in which this cell is located in the matrix
   */
  def getRow: Int

  /**
   * @return the column in which this cell is located in the matrix
   */
  def getCol: Int

  /**
   * Moves the content of this cell to another random
   *  adjacent cell
   */
  def moveToRandomAdjacentCell()

  /**
   * Adds the specified cell to the list of
   *  adjacent cells of this cell
   *
   * @param cell the cell to be added
   */
  def addAdjacentCell(cell: Cell)

  /**
   * Sets the specified monster into this cell
   *
   * @param monster the monster to be set
   */
  def setMonster(monster: Monster)

  /**
   * @return the monster into this cell
   */
  def getMonster: Monster

  /**
   * Remove the content that exists inside
   *  this cell
   */
  def removeMonster(): Unit
}

class MonsterCell(val row: Int, val col: Int) extends Cell {

  private var monster: Monster = null

  /**
   * a list containing all adjacent cells of this cell
   */
  private val adjacentCells = new ListBuffer[Cell]

  /**
   *
   */
  override def moveToRandomAdjacentCell() = synchronized {
    require(null != getMonster, "Monster cannot be null, " +
      "since this method is called by a monster that is inside this cell (race condition?)")
    val randomCell = getRandomAdjacentCell
    if (randomCell.isEmpty) {
      val innerMonster = getMonster
      getMonster.removeRelWithCel()
      innerMonster.createRelWithCel(randomCell)
    } /* else wait for the next turn of moving. */
  }

  override def addAdjacentCell(cell: Cell) = synchronized { adjacentCells += cell }

  override def removeMonster(): Unit = synchronized { monster = null }

  override def setMonster(monster: Monster): Unit = synchronized {
    require(null != monster, "monster being set to a cell cannot be null")
    this.monster = monster
  }

  override def getMonster: Monster = synchronized { monster }

  override def isEmpty: Boolean = getMonster == null

  override def getRow: Int = row

  override def getCol: Int = col

  override def toString: String = "Cell[" + row + ", " + col + "]"

  /**
   * @return return a random adjacent cell of this cell
   */
  private def getRandomAdjacentCell: Cell = synchronized { adjacentCells(Random.nextInt(adjacentCells.size)) }

}
