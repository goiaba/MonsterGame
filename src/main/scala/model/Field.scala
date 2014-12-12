package edu.luc.cs.comp413.scala.monstergame.model

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by bruno on 04/12/14.
 */

/**
 * This trait represents a matrix of cells that can host monsters
 *  inside them
 */
trait Field {

  /**
   *
   * @return the number of columns that composes
   *          this field
   */
  def getNumberOfColumns: Int

  /**
   *
   * @return the number of rows that composes
   *          this field
   */
  def getNumberOfRows: Int

  /**
   *
   * @param row the row of the cell in which the monster
   *             is located
   * @param col the col of the cell in which the monster
   *             is located
   * @return
   */
  def getMonsterFromCell(row: Int, col: Int): Monster

  /**
    * Put a new monster in a random empty cell
   *
   * @return the created monster
   */
  def putMonsterInARandomCell(monster: Monster): Boolean

  /**
   * Insert the specified cell in the list of empty
   *  (available) cells if it is really empty
   *
   * @param cell the cell to be inserted in the list
   *              of available cells
   */
  def releaseCell(cell: Cell): Unit

}

class MonsterField(val rows: Int, val cols: Int) extends Field {

  private val cellsGrid = Array.ofDim[Cell](rows, cols)

  private val emptyCellsInTheGrid = new ListBuffer[Cell]

  { initCells() }

  /**
   *
   * @return the number of columns that composes
   *         this field
   */
  override def getNumberOfColumns: Int = cols

  /**
   *
   * @return the number of rows that composes
   *         this field
   */
  override def getNumberOfRows: Int = rows

  override def getMonsterFromCell(row: Int, col: Int): Monster = {
    val cell = cellsGrid(row)(col)
    cell.getMonster
  }

  /**
   *
   * @return a random available cell in this field
   */
  def getRandomAvailableCell: Cell = {
    if (emptyCellsInTheGrid.nonEmpty) {
      val size = emptyCellsInTheGrid.size
      val cell = emptyCellsInTheGrid((Random.nextFloat() * size).toInt)
      cell
    } else {
      null
    }
  }

  /**
   * Put a new monster in a random empty cell
   *
   * @return the created monster
   */
  override def putMonsterInARandomCell(monster: Monster): Boolean = {
    val cell = getRandomAvailableCell
    if (null != cell) {
      cell.setMonster(monster)
      monster.setCell(cell)
      emptyCellsInTheGrid -= cell
      true
    } else false
  }

  /**
   * Insert the specified cell in the list of empty
   *  (available) cells
   */
  override def releaseCell(cell: Cell): Unit = {
    require(cell != null, "cell cannot be null")
    cell.removeMonster()
    emptyCellsInTheGrid += cell
  }

  /**
    * The rule of the game specifies that is only possible to
   *  move to adjacent cells. So, to facilitate we initially
   *  set the adjacent cells of each cell in the field.
   */
  private def initCells() = {

    /* initialize each cell */
    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val emptyMonsterCell = new MonsterCell(row, col)
        cellsGrid(row)(col) = emptyMonsterCell
        emptyCellsInTheGrid += emptyMonsterCell
      }
    }

    /* set adjacent cells */
    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val currentCell: Cell = cellsGrid(row)(col)
        if (row > 0) {
          if (col > 0) currentCell.addAdjacentCell(cellsGrid(row - 1)(col - 1))
          currentCell.addAdjacentCell(cellsGrid(row - 1)(col))
          if (col < cols - 1) currentCell.addAdjacentCell(cellsGrid(row - 1)(col + 1))
        }
        if (col > 0) currentCell.addAdjacentCell(cellsGrid(row)(col - 1))
        if (col < cols - 1) currentCell.addAdjacentCell(cellsGrid(row)(col + 1))
        if (row < rows - 1) {
          if (col > 0) currentCell.addAdjacentCell(cellsGrid(row + 1)(col - 1))
          currentCell.addAdjacentCell(cellsGrid(row + 1)(col))
          if (col < cols - 1) currentCell.addAdjacentCell(cellsGrid(row + 1)(col + 1))
        }
      }
    }
  }
}
