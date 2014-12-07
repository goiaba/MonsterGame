package edu.luc.etl.cs313.scala.uidemo.model

import android.util.Log

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by bruno on 04/12/14.
 */
trait Cell {
  def isEmpty(): Boolean
  def getRow(): Int
  def getCol(): Int
  def getAdjacentCells(): List[Cell]
  def getRandomAdjacentCell(): Cell
  def moveToRandomAdjacentCell()
  def addAdjacentCell(cell: Cell)
  def setMonster(monster: Monster)
  def getMonster(): Monster
  def removeMonster()
}

class MonsterCell(val row: Int, val col: Int) extends Cell {

  private var monster: Monster = null
  private val adjacentCells = new ListBuffer[Cell]

  override def getAdjacentCells(): List[Cell] = adjacentCells.toList

  override def getRandomAdjacentCell(): Cell = {
    val index = Random.nextInt(adjacentCells.size)
    adjacentCells(index)
  }

  override def moveToRandomAdjacentCell() = {
    val randomCell = getRandomAdjacentCell()
    if (randomCell.isEmpty) {
      randomCell.setMonster(monster)
      this.monster.setCell(randomCell)
      removeMonster()
    }
  }

  override def addAdjacentCell(cell: Cell) = adjacentCells += cell

  override def removeMonster(): Unit = monster = null

  override def setMonster(monster: Monster): Unit = this.monster = monster

  override def getMonster(): Monster = monster

  override def getRow(): Int = row

  override def getCol(): Int = col

  override def isEmpty(): Boolean = monster == null

  override def toString(): String = {
  "[" + row + ", " + col + "]"
  }
}
