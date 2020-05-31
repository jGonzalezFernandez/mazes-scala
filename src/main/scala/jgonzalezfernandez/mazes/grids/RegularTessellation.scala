package jgonzalezfernandez.mazes.grids

import jgonzalezfernandez.mazes.Utils.PositiveInt

trait RegularTessellation extends Grid {

  def columns: PositiveInt

  override val gridCells: Vector[Vector[Cell]] = Vector.tabulate[Cell](rows.value, columns.value)((row, column) => Cell(row, column))

}
