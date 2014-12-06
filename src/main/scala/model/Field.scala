package edu.luc.etl.cs313.scala.uidemo.model

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

  def getMonsterFromCell(row: Int, col: Int): Monster

  /**
    * Put a new monster in a random empty cell
   *
   * @return the created monster
   */
  def putMonsterInARandomCell(vulnerableTime: Long): Monster

  /**
    * Remove the monster of the specified cell
   *
   * @param row
   * @param col
   * @return the removed monster
   */
  def removeMonsterFromTheCell(row: Int, col: Int)

  //  /**
  //   * @param cell the cell which we want to know the position
  //   *              in this field
  //   * @return a tuple composed by row (_1) and col (_2)
  //   */
  //  def getCellRowAndCol(cell: Cell): (Int, Int)

  //  /**
  //   *
  //   * @param cellPos
  //   * @return
  //   */
  //  def getCellByPosition(cellPos: Int): Cell

//  /**
//   * Putz a monster in the specified cell
//   *
//   * @param monster the monster to be put in the cell
//   * @param row the row of the chosen cell
//   * @param col the col of the chosen cell
//   */
//  def putMonsterInTheCell(monster: Monster, row: Int, col: Int)

//  /**
//   * Puts a monster in the specified cell
//   *
//   * @param monster the monster to be put in the cell
//   * @param cellPos the chosen cell
//   */
//  def putMonsterInTheCell(monster: Monster, cellPos: Int)

}

class MonsterField(val rows: Int, val cols: Int) extends Field {

  private var monstersInTheField = 0

  private val cellsGrid = Array.ofDim[Cell](rows, cols)

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

  override def getMonsterFromCell(row: Int, col: Int): Monster = {
    val cell = cellsGrid(row)(col)
    cell.getMonster()
  }

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
   * Put a new monster in a random empty cell
   *
   * @return the created monster
   */
  override def putMonsterInARandomCell(vulnerableTime: Long): Monster = {
    val cell = getRandomAvailableCell()
    if (null != cell) {
      emptyCellsInTheGrid -= cell
      val newMonster = new Monster(cell, vulnerableTime)
      cell.setMonster(newMonster)
      monstersInTheField += 1
      newMonster
    } else null
  }

  /**
   * Remove the monster of the specified cell
   *
   * @param row
   * @param col
   * @return the removed monster
   */
  override def removeMonsterFromTheCell(row: Int, col: Int) = {
    val cell = cellsGrid(row)(col)
    val monster = cell.getMonster()
    if (null != monster) {
      cell.removeMonster()
      monstersInTheField -= 1
      emptyCellsInTheGrid += cell
    }
  }

//  /**
//   * @param cell the cell which we want to know the position
//   *              in this field
//   * @return a tuple composed by row (_1) and col (_2)
//   */
//  override def getCellRowAndCol(cell: Cell): (Int, Int) = {
//    @tailrec
//    def getCellRowAndColRec(row: Int, col: Int): (Int, Int) = {
//      if (row == cellsGrid.length) (-1, -1)
//      else if (col == cellsGrid(row).length) getCellRowAndColRec(row + 1, 0)
//      else if (cellsGrid(row)(col) == cell) (row, col)
//      else getCellRowAndColRec(row, col + 1)
//    }
//    getCellRowAndColRec(0, 0)
//  }

  //  /**
  //   * cells are arranged from left to right and up to down. For
  //   *  example, in a cellsGrid of 3 rows and 4 columns (3x4),
  //   *  the fieldPos 9 is located under row 2 and column 1 (zero
  //   *  based index).
  //   *
  //   * @param cellPos the fieldPos to retrieve
  //   * @return the Cell contained in the specific
  //   *          position.
  //   */
  //  override def getCellByPosition(cellPos: Int): Cell = {
  //    val row = (cellPos / rows).toInt
  //    val col = cellPos % rows
  //    if (row > rows-1 || col > col-1)
  //      throw new RuntimeException("Index out of bounds.")
  //    cellsGrid(row)(col)
  //  }

  //  override def putMonsterInTheCell(monster: Monster, row: Int, col: Int) = {
  //    val cell = cellsGrid(row)(col)
  //    emptyCellsInTheGrid -= cell
  //    cell.setMonster(monster)
  //    monstersInTheField += 1
  //  }

  //  override def putMonsterInTheCell(monster: Monster, cellPos: Int) = {
  //    val cell = getCellByPosition(cellPos)
  //    emptyCellsInTheGrid -= cell
  //    cell.setMonster(monster)
  //    monstersInTheField += 1
  //  }


  /**
    * The rule of the game specifies that is only possible to
   *  move to adjacent cells. So, to facilitate we initially
   *  set the adjacent cells of each cell in the field.
   */
  private def createAdjacentCellsList = {

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
        if (row > 0) {
          if (col > 0) cellsGrid(row)(col).addAdjacentCell(cellsGrid(row - 1)(col - 1));
          cellsGrid(row)(col).addAdjacentCell(cellsGrid(row - 1)(col));
          if (col < cols - 1) cellsGrid(row)(col).addAdjacentCell(cellsGrid(row - 1)(col + 1));
        }
        if (col > 0) cellsGrid(row)(col).addAdjacentCell(cellsGrid(row)(col - 1));
        if (col < cols - 1) cellsGrid(row)(col).addAdjacentCell(cellsGrid(row)(col + 1));
        if (row < rows - 1) {
          if (col > 0) cellsGrid(row)(col).addAdjacentCell(cellsGrid(row + 1)(col - 1));
          cellsGrid(row)(col).addAdjacentCell(cellsGrid(row + 1)(col));
          if (col < cols - 1) cellsGrid(row)(col).addAdjacentCell(cellsGrid(row + 1)(col + 1));
        }
      }
    }
  }
}
