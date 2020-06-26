package jgonzalezfernandez.mazes.grids

import scala.collection.mutable

final case class Cell(row: Int, column: Int) {
  var weight: Int            = 1
  var isEnd: Boolean         = false        // the furthest point from the start (which is determined by the grid type)
  var distanceFromStart: Int = Int.MaxValue // will be updated by Dijkstra's algorithm
  private val links          = mutable.Set.empty[Cell]

  def linkTo(cell: Cell, reciprocal: Boolean = true): Unit = {
    links += cell
    if (reciprocal) cell.links += this
  }

  def unlinkFrom(cell: Cell, reciprocal: Boolean = true): Unit = {
    links -= cell
    if (reciprocal) cell.links -= this
  }

  def isLinkedTo(cell: Cell): Boolean = links.contains(cell) // directly linked

}
