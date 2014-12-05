package edu.luc.etl.cs313.scala.uidemo.model

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by bruno on 04/12/14.
 */
trait Cell {
  def getAdjacentCells(): List[Cell]
  def getRandomAdjacentCell(): Cell
  def moveToRandomAdjacentCell()
  def addAdjacentCell(cell: Cell)
  def setMonster(monster: Monster)
  def getMonster(): Monster
  def removeMonster()
}

class MonsterCell extends Cell {

  private var monster: Monster = null
  private val adjacentCells = new ListBuffer[Cell]

  override def getAdjacentCells(): List[Cell] = adjacentCells.toList

  def getRandomAdjacentCell(): Cell = {
    val index = (Random.nextFloat() * adjacentCells.size).toInt
    adjacentCells(index)
  }

  def moveToRandomAdjacentCell() = {
    getRandomAdjacentCell().setMonster(monster)
    removeMonster()
  }

  def addAdjacentCell(cell: Cell) = adjacentCells += cell

  override def removeMonster(): Unit = monster = null

  override def setMonster(monster: Monster): Unit = this.monster = monster

  override def getMonster(): Monster = monster
}
