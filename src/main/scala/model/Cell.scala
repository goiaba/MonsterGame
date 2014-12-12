package edu.luc.cs.comp413.scala.monstergame.model

import java.util.concurrent.Semaphore

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
   * @return a list containing all adjacent cells of this cell
   */
  def getAdjacentCells: List[Cell]

  /**
   * @return return a random adjacent cell of this cell
   */
  def getRandomAdjacentCell: Cell

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
  private val adjacentCells = new ListBuffer[Cell]

  override def getAdjacentCells: List[Cell] = adjacentCells.toList

  override def getRandomAdjacentCell: Cell = {
    val index = Random.nextInt(adjacentCells.size)
    adjacentCells(index)
  }

  override def moveToRandomAdjacentCell() = {
    val randomCell = getRandomAdjacentCell
    if (randomCell.isEmpty && null != monster) {
      randomCell.setMonster(monster)
      monster.setCell(randomCell)
      removeMonster()
    }
  }

  override def addAdjacentCell(cell: Cell) = adjacentCells += cell

  override def removeMonster(): Unit = monster = null

  override def setMonster(monster: Monster): Unit = this.monster = monster

  override def getMonster: Monster = monster

  override def getRow: Int = row

  override def getCol: Int = col

  override def isEmpty: Boolean = monster == null

  override def toString: String = "[" + row + ", " + col + "]"
}
