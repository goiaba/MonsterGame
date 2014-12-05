package edu.luc.etl.cs313.scala.uidemo.model

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by bruno on 04/12/14.
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
   * @return a list containing all available cells in this
   *          field
   */
  def getAvailableCells(): List[Cell]

  /**
   *
   * @return a random available cell in this field
   */
  def getRandomAvailableCell(): Cell

  /**
   *
   * @param cellPos
   * @return
   */
  def getCellByPosition(cellPos: Int): Cell

  def getCellRowAndCol(cell: Cell): (Int, Int)

  /**
   * Putz a monster in the specified cell
   *
   * @param monster the monster to be put in the cell
   * @param row the row of the chosen cell
   * @param col the col of the chosen cell
   */
  def putMonsterInTheCell(monster: Monster, row: Int, col: Int)

  /**
   * Putz a monster in the specified cell
   *
   * @param monster the monster to be put in the cell
   * @param cellPos the chosen cell
   */
  def putMonsterInTheCell(monster: Monster, cellPos: Int)

  /**
   * Puts a new monster in a random empty cell
   *
   * @return the monster created
   */
  def putMonsterInACell(vulnerableTime: Long): Monster

  def removeMonsterFromTheCell(row: Int, col: Int): Monster

}

class MonsterField(val rows: Int, val cols: Int) extends Field {

  private val cellsGrid = Array.ofDim[Cell](rows, cols)

  private var monstersInTheField = 0

  private val emptyCellsInTheGrid = new ListBuffer[Cell]

  { createAdjacentCellsList }

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
   * @return a list containing all available cells in this
   *         field
   */
  override def getAvailableCells(): List[Cell] = emptyCellsInTheGrid.toList

  /**
   *
   * @return a random available cell in this field
   */
  override def getRandomAvailableCell(): Cell = {
    if (!emptyCellsInTheGrid.isEmpty) {
      val size = emptyCellsInTheGrid.size
      val cell = emptyCellsInTheGrid((Random.nextFloat() * size).toInt)
      cell
    } else {
      null
    }
  }

  /**
   * cells are arranged from left to right and up to down. For
   *  example, in a cellsGrid of 3 rows and 4 columns (3x4),
   *  the fieldPos 9 is located under row 2 and column 1 (zero
   *  based index).
   *
   * @param cellPos the fieldPos to retrieve
   * @return the Cell contained in the specific
   *          position.
   */
  override def getCellByPosition(cellPos: Int): Cell = {
    val row = (cellPos / rows).toInt
    val col = cellPos % rows
    if (row > rows-1 || col > col-1)
      throw new RuntimeException("Index out of bounds.")
    cellsGrid(row)(col)
  }

  override def getCellRowAndCol(cell: Cell): (Int, Int) = {
    @tailrec
    def getCellRowAndColRec(row: Int, col: Int): (Int, Int) = {
      if (row == cellsGrid.length) (-1, -1)
      else if (col == cellsGrid(row).length) getCellRowAndColRec(row + 1, 0)
      else if (cellsGrid(row)(col) == cell) (row, col)
      else getCellRowAndColRec(row, col + 1)
    }
    getCellRowAndColRec(0, 0)
  }

  override def putMonsterInTheCell(monster: Monster, row: Int, col: Int) = {
    val cell = cellsGrid(row)(col)
    emptyCellsInTheGrid -= cell
    cell.setMonster(monster)
    monstersInTheField += 1
  }

  override def putMonsterInTheCell(monster: Monster, cellPos: Int) = {
    val cell = getCellByPosition(cellPos)
    emptyCellsInTheGrid -= cell
    cell.setMonster(monster)
    monstersInTheField += 1
  }

  override def putMonsterInACell(vulnerableTime: Long): Monster = {
    val cell = getRandomAvailableCell()
    if (null != cell) {
      val rowColTuple = getCellRowAndCol(cell)
      emptyCellsInTheGrid -= cell
      val newMonster = new Monster(rowColTuple._1, rowColTuple._2, vulnerableTime)
      cell.setMonster(newMonster)
      monstersInTheField += 1
      newMonster
    } else null
  }

  override def removeMonsterFromTheCell(row: Int, col: Int): Monster = {
    val cell = cellsGrid(row)(col)
    val monster = cell.getMonster()
    cell.removeMonster()
    monstersInTheField -= 1
    emptyCellsInTheGrid += cell
    monster
  }

  /**
    * The rule of the game specifies that is only possible to
   *  move to adjacent cells. So, to facilitate we initially
   *  set the adjacent cells of each cell in the field.
   */
  private def createAdjacentCellsList = {
    for (i <- 0 to rows-1) {
      for (j <- 0 to cols-1) {

        val emptyMonsterCell = new MonsterCell
        cellsGrid(i)(j) = emptyMonsterCell
        emptyCellsInTheGrid += emptyMonsterCell

        if (i > 0) {
          if (j > 0) cellsGrid(i)(j).addAdjacentCell(cellsGrid(i - 1)(j - 1))
          cellsGrid(i)(j).addAdjacentCell(cellsGrid(i - 1)(j))
          if (j < cols - 1) cellsGrid(i)(j).addAdjacentCell(cellsGrid(i - 1)(j + 1))
        }
        if (j > 0) cellsGrid(i)(j).addAdjacentCell(cellsGrid(i)(j - 1))
        if (j < cols - 1) cellsGrid(i)(j).addAdjacentCell(cellsGrid(i)(j + 1))
        if (i < rows - 1) {
          if (j > 0) cellsGrid(i)(j).addAdjacentCell(cellsGrid(i + 1)(j - 1))
          cellsGrid(i)(j).addAdjacentCell(cellsGrid(i + 1)(j))
          if (j < cols - 1) cellsGrid(i)(j).addAdjacentCell(cellsGrid(i + 1)(j + 1))
        }
      }
    }
  }

}
