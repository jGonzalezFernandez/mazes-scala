package mazes.algorithms

import mazes.grids.Grid

import scala.collection.mutable

trait RecursiveBacktrackerAlgo extends Algorithm { // AKA Depth-First Search algorithm

  def applyAlgorithm(grid: Grid): Grid = {
    val visitedCells           = mutable.Set.empty[grid.Cell]
    val randomStartingPosition = grid.getRandomCell
    val stack                  = mutable.Stack[grid.Cell](randomStartingPosition)

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
