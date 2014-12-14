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
   *         this field
   */
  def getNumberOfColumns: Int

  /**
   *
   * @return the number of rows that composes
   *         this field
   */
  def getNumberOfRows: Int

  /**
   *
   * @param row the row of the cell in which the monster
   *            is located
   * @param col the col of the cell in which the monster
   *            is located
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
   * (available) cells if it is really empty
   *
   * @param cell the cell to be inserted in the list
   *             of available cells
   */
  def releaseCell(cell: Cell): Unit

  def reset(): Unit

}

class MonsterField(private val rows: Int, private val cols: Int) extends Field {

  private val cellsGrid = Array.ofDim[Cell](rows, cols)

  private val emptyCellsInTheGrid = new ListBuffer[Cell]

  { reset() }

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

  /**
   *
   * @param row the row of the cell in which the monster
   *            is located
   * @param col the col of the cell in which the monster
   *            is located
   * @return
   */
  override def getMonsterFromCell(row: Int, col: Int): Monster = synchronized { cellsGrid(row)(col).getMonster }

  /**
    * Put a new monster in a random empty cell
   *
   * @return the created monster
   */
  override def putMonsterInARandomCell(monster: Monster): Boolean = synchronized {
    val cell = getRandomAvailableCell
    if (null != cell) {
      monster.createRelWithCel(cell)
      true
    } else false
  }

  /**
   * Insert the specified cell in the list of empty
   *  (available) cells
   */
  override def releaseCell(cell: Cell): Unit = synchronized {
    require(cell != null, "cell cannot be null")
    emptyCellsInTheGrid += cell
  }

  override def reset(): Unit = synchronized {
    initCells()
    initAdjacencyOfCells()
  }

  /**
   *
   * @return a random available cell in this field
   */
  private def getRandomAvailableCell: Cell = synchronized {
    if (emptyCellsInTheGrid.nonEmpty)
      emptyCellsInTheGrid.remove(Random.nextInt(emptyCellsInTheGrid.size))
    else null
  }

  private def initCells(): Unit = {
    emptyCellsInTheGrid.clear()
    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val emptyMonsterCell: Cell = new MonsterCell(row, col)
        cellsGrid(row)(col) = emptyMonsterCell
        emptyCellsInTheGrid += emptyMonsterCell
      }
    }
  }

  /**
    * The rule of the game specifies that is only possible to
   *  move to adjacent cells. So, to facilitate we initially
   *  set the adjacent cells of each cell in the field.
   */
  private def initAdjacencyOfCells() = {

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
