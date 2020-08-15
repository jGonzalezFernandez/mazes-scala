package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.Utils.randomInt
import jgonzalezfernandez.mazes.grids.{Cell, Grid}

import scala.collection.mutable.ArrayBuffer

object Sidewinder extends Algorithm[Grid] {

  def applyAlgorithm(grid: Grid, startingPositionOpt: Option[Cell]): Grid = {

    def randomlyCloseRun: Boolean = randomInt(2) == 0

    def closeRun(run: ArrayBuffer[Cell]): Unit =
      for {
        randomCandidate <- grid.getRandomCell(run)
        randomNorthCell <- grid.getNorthCellOf(randomCandidate)
      } {
        randomCandidate.linkTo(randomNorthCell)
        run.clear()
      }

    val run = ArrayBuffer.empty[Cell]

    for (row <- grid.indexedCells.indices) {
      for (cell <- grid.indexedCells(row)) {
        val northCellOpt = grid.getNorthCellOf(cell)
        val eastCellOpt  = grid.getEastCellOf(cell)

        if (northCellOpt.nonEmpty) run += cell // a little hack to improve the output on Triangular grids

        (northCellOpt, eastCellOpt) match {
          case (None, Some(eastCell))    => cell.linkTo(eastCell)
          case (_, None)                 => closeRun(run)
          case (Some(_), Some(eastCell)) => if (randomlyCloseRun) closeRun(run) else cell.linkTo(eastCell)
        }
      }
    }

    grid
  }

}
