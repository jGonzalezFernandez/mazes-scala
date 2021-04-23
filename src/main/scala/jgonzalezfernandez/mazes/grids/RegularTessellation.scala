package jgonzalezfernandez.mazes.grids

import jgonzalezfernandez.mazes.Utils.TwoTo100

trait RegularTessellation extends Grid {

  def columns: TwoTo100

  val indexedCells: Vector[Vector[Cell]] = Vector.tabulate[Cell](rows.value, columns.value)((row, column) => Cell(row, column))

  val startingCell: Cell = indexedCells(rows.value - 1)(0)

}
