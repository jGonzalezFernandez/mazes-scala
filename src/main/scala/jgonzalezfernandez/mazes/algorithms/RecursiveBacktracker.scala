package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

import scala.collection.mutable

object RecursiveBacktracker extends Algorithm[Grid] { // AKA Depth-First Search algorithm

  /* Here’s the mile-high view of recursive backtracking:
   * 1. Choose a starting point in the field.
   * 2. Randomly choose a wall at that point and carve a passage through to the adjacent cell, but only if the adjacent
   *    cell has not been visited yet. This becomes the new current cell.
   * 3. If all adjacent cells have been visited, back up to the last cell that has uncarved walls and repeat.
   * 4. The algorithm ends when the process has backed all the way up to the starting point.
   * Source: http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
   */

  def applyAlgorithm(grid: Grid, startingPositionOpt: Option[Cell]): Grid = {
    val visitedCells     = mutable.Set.empty[Cell]
    val startingPosition = startingPositionOpt.getOrElse(grid.getRandomCell)
    val stack            = mutable.Stack[Cell](startingPosition)

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
