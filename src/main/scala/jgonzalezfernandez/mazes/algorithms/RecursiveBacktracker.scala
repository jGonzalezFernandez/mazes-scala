package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

import scala.collection.mutable

object RecursiveBacktracker extends Algorithm { // AKA Depth-First Search algorithm

  def applyAlgorithm(grid: Grid): Grid = {
    val visitedCells           = mutable.Set.empty[Cell]
    val randomStartingPosition = grid.getRandomCell
    val stack                  = mutable.Stack[Cell](randomStartingPosition)

    while (stack.nonEmpty) {
      val currentCell = stack.top
      visitedCells += currentCell
      val candidateNeighbours = grid.getNeighboursOf(currentCell).filterNot(visitedCells.contains)
      val randomCandidateOpt  = grid.getRandomCell(candidateNeighbours)
      randomCandidateOpt match {
        case Some(randomCandidate) =>
          currentCell.linkTo(randomCandidate)
          stack.push(randomCandidate)
        case None => stack.pop()
      }
    }

    grid
  }

}
