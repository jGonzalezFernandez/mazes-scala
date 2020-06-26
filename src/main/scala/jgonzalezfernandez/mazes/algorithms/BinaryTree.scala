package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

object BinaryTree extends Algorithm[Grid] {

  def applyAlgorithm(grid: Grid, startingPositionOpt: Option[Cell]): Grid = {

    grid.allCells.foreach { cell =>
      val candidateNeighbours = Seq(grid.getNorthCellOf(cell), grid.getEastCellOf(cell)).flatten
      val randomCandidateOpt  = grid.getRandomCell(candidateNeighbours)
      randomCandidateOpt match {
        case Some(randomCandidate) => cell.linkTo(randomCandidate)
        case None                  =>
      }
    }

    grid
  }

}
