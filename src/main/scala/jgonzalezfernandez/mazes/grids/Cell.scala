package jgonzalezfernandez.mazes.grids

import scala.collection.mutable

final case class Cell(row: Int, column: Int) {
  var isStart: Boolean = false
  var isEnd: Boolean   = false
  private val links    = mutable.Set.empty[Cell]

  def linkTo(cell: Cell): Unit = {
    links += cell
    cell.links += this
  }

  def unlinkFrom(cell: Cell): Unit = {
    links -= cell
    cell.links -= this
  }

  def isLinkedTo(cell: Cell): Boolean = links.contains(cell) // directly linked

}
