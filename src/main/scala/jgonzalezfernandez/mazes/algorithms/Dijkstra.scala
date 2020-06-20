package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

import scala.collection.mutable

object Dijkstra extends Algorithm {

  def applyAlgorithm(grid: Grid, startingPositionOpt: Option[Cell]): Grid = {
    val startingPosition = startingPositionOpt.getOrElse(grid.getRandomCell)
    startingPosition.distanceFromStart = 0

    val sortByWeight: Ordering[Cell] = (a, b) => a.weight.compareTo(b.weight)
    val priorityQueue                = mutable.PriorityQueue[Cell](startingPosition)(sortByWeight)

    while (priorityQueue.nonEmpty) {
      val currentCell = priorityQueue.dequeue()
      val neighbours  = grid.getNeighboursOf(currentCell).filter(currentCell.isLinkedTo)

      neighbours.foreach { neighbour =>
        val candidatePath = currentCell.distanceFromStart + neighbour.weight
        if (neighbour.distanceFromStart > candidatePath) {
          neighbour.distanceFromStart = candidatePath
          if (!priorityQueue.exists(_ == neighbour)) priorityQueue.enqueue(neighbour)
        }
      }
    }

    grid
  }

}
