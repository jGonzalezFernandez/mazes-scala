package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.Utils.randomInt
import jgonzalezfernandez.mazes.grids.{Cell, Grid, SquareGrid}

object RecursiveDivision extends Algorithm[SquareGrid] {

  // TODO: generalize this implementation to work with any type of grid
  def applyAlgorithm(grid: SquareGrid, startingPositionOpt: Option[Cell]): Grid = {

    def divide(grid: Grid, row: Int, column: Int, height: Int, width: Int): Grid = {
      if (height <= 1 || width <= 1) grid
      else if (height > width) divideHorizontally(grid, row, column, height, width)
      else divideVertically(grid, row, column, height, width)
    }

    def divideHorizontally(grid: Grid, row: Int, column: Int, height: Int, width: Int): Grid = {
      val cutPosition = randomInt(height - 1)
      val passageAt   = randomInt(width)

      for (x <- 0.until(width)) {
        if (x != passageAt) {
          val currentCell = grid.indexedCells(row + cutPosition)(column + x)
          grid.getSouthCellOf(currentCell).foreach(southCell => currentCell.unlinkFrom(southCell))
        }
      }

      divide(grid, row, column, cutPosition + 1, width)
      divide(grid, row + cutPosition + 1, column, height - cutPosition - 1, width)
    }

    def divideVertically(grid: Grid, row: Int, column: Int, height: Int, width: Int): Grid = {
      val cutPosition = randomInt(width - 1)
      val passageAt   = randomInt(height)

      for (y <- 0.until(height)) {
        if (y != passageAt) {
          val currentCell = grid.indexedCells(row + y)(column + cutPosition)
          grid.getEastCellOf(currentCell).foreach(eastCell => currentCell.unlinkFrom(eastCell))
        }
      }

      divide(grid, row, column, height, cutPosition + 1)
      divide(grid, row, column + cutPosition + 1, height, width - cutPosition - 1)
    }

    grid.allCells.foreach { cell =>
      val neighbours = grid.getNeighboursOf(cell)
      neighbours.foreach(cell.linkTo(_, reciprocal = false)) // since we are iterating over all cells, we don't need to establish bidirectional links
    }

    divide(grid, 0, 0, grid.rows.value, grid.columns.value)
  }

}
