package jgonzalezfernandez.mazes.grids

import jgonzalezfernandez.mazes.Utils.PositiveInt

trait RegularTessellation extends Grid {

  def columns: PositiveInt

  val indexedCells: Vector[Vector[Cell]] = Vector.tabulate[Cell](rows.value, columns.value)((row, column) => Cell(row, column))

  val startingCell: Cell = indexedCells(rows.value - 1)(0)

}
