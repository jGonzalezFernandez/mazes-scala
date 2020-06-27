package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.Utils.randomInt
import jgonzalezfernandez.mazes.grids.{Cell, Grid, SquareGrid}

// TODO: generalize these functions to work with any type of grid
// TODO: make the divide def tail recursive
object RecursiveDivision extends Algorithm[SquareGrid] {

  // Since we are going to recursively divide a single grid by adding walls, for each iteration it is necessary to delimit the
  // area of the grid in which we are going to work (row + height = vertical limit, and column + width = horizontal limit).
  def divide(grid: SquareGrid, row: Int, column: Int, height: Int, width: Int): Grid = {

    def horizontalCut: Grid = {
      val cutPosition     = randomInt(height - 1)
      val passagePosition = randomInt(width)

      for (x <- 0 until width) {
        if (x != passagePosition) {
          val currentCell = grid.indexedCells(row + cutPosition)(column + x)
          grid.getSouthCellOf(currentCell).foreach(southCell => currentCell.unlinkFrom(southCell))
        }
      }

      divide(grid, row, column, cutPosition + 1, width)
      divide(grid, row + cutPosition + 1, column, height - cutPosition - 1, width)
    }

    def verticalCut: Grid = {
      val cutPosition     = randomInt(width - 1)
      val passagePosition = randomInt(height)

      for (y <- 0 until height) {
        if (y != passagePosition) {
          val currentCell = grid.indexedCells(row + y)(column + cutPosition)
          grid.getEastCellOf(currentCell).foreach(eastCell => currentCell.unlinkFrom(eastCell))
        }
      }

      divide(grid, row, column, height, cutPosition + 1)
      divide(grid, row, column + cutPosition + 1, height, width - cutPosition - 1)
    }

    def randomlyBuildRoom: Boolean = height < 5 && width < 5 && randomInt(4) == 0

    if (height <= 1 || width <= 1 || randomlyBuildRoom) grid // recursion stops
    else if (height > width) horizontalCut                   // we could have chosen at random, but using the aspect ratio gives better results
    else verticalCut
  }

  def applyAlgorithm(grid: SquareGrid, startingPositionOpt: Option[Cell]): Grid = {
    grid.allCells.foreach { cell =>
      val neighbours = grid.getNeighboursOf(cell)
      neighbours.foreach(cell.linkTo(_, reciprocal = false)) // we're iterating over all the cells, so we don't need to link them bilaterally
    }

    divide(grid, 0, 0, grid.rows.value, grid.columns.value)
  }

}
