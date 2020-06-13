package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

import scala.collection.mutable

object Dijkstra extends Algorithm {

  // Dijkstra's algorithm is normally used to produce the shortest path tree from a given node, but we are going to apply
  // it to do just the opposite: find the two most distant cells in the maze and mark them as Start and End.
  def applyAlgorithm(grid: Grid): Grid = {
    val startingCell = grid.gridCells(0)(0)
    startingCell.isStart = true
    startingCell.distanceFromStart = 0

    var endCell = startingCell

    val sortByWeight: Ordering[Cell] = (a, b) => a.weight.compareTo(b.weight)
    val queue                        = mutable.PriorityQueue[Cell](startingCell)(sortByWeight)

    while (queue.nonEmpty) {
      val currentCell = queue.dequeue()
      val neighbours  = grid.getNeighboursOf(currentCell).filter(currentCell.isLinkedTo)

      neighbours.foreach { neighbour =>
        val candidatePath = currentCell.distanceFromStart + neighbour.weight
        if (neighbour.distanceFromStart > candidatePath) {
          neighbour.distanceFromStart = candidatePath
          if (!queue.exists(_ == neighbour)) queue.enqueue(neighbour)

          if (neighbour.distanceFromStart > endCell.distanceFromStart) {
            endCell = currentCell
          }
        }
      }
    }

    //    endCell.isEnd = true

    def max(a: Cell, b: Cell): Cell = if (a.distanceFromStart > b.distanceFromStart) a else b

    val bob = grid.gridCells.flatten.reduceLeft(max)

    bob.isEnd = true

    grid

  }

}
