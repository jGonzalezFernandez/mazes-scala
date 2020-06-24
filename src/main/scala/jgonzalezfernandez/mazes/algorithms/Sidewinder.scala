package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.Utils.randomInt
import jgonzalezfernandez.mazes.grids.{Cell, Grid}

import scala.collection.mutable.ArrayBuffer

object Sidewinder extends Algorithm {

  def applyAlgorithm(grid: Grid, startingPositionOpt: Option[Cell]): Grid = {

    def randomlyCloseRun: Boolean = randomInt(2) == 0

    def closeRun(run: ArrayBuffer[Cell]): Unit = {
      val randomCandidate    = grid.getRandomCell(run).get
      val randomNorthCellOpt = grid.getNorthCellOf(randomCandidate)
      randomNorthCellOpt.foreach(randomCandidate.linkTo)
      run.clear()
    }

    val run = ArrayBuffer.empty[Cell]

    for (row <- grid.indexedCells.indices) {
      for (column <- grid.indexedCells(row).indices) {
        val currentCell  = grid.indexedCells(row)(column)
        val northCellOpt = grid.getNorthCellOf(currentCell)
        val eastCellOpt  = grid.getEastCellOf(currentCell)

        run += currentCell

        (northCellOpt, eastCellOpt) match {
          case (None, Some(eastCell))    => currentCell.linkTo(eastCell)
          case (_, None)                 => closeRun(run)
          case (Some(_), Some(eastCell)) => if (randomlyCloseRun) closeRun(run) else currentCell.linkTo(eastCell)
        }
      }
    }

    grid
  }

}
