package jgonzalezfernandez.mazes.algorithms

import eu.timepit.refined.auto._
import jgonzalezfernandez.mazes.Utils.PositiveInt
import jgonzalezfernandez.mazes.grids.SquareGrid
import org.scalatest.wordspec.AnyWordSpec

class DijkstraSpec extends AnyWordSpec {

  "Dijkstra algorithm" should {

    val rows: PositiveInt    = 5
    val columns: PositiveInt = 5

    "Produce the shortest-path tree from a given starting point, taking into account the cell weights" in {
      val grid = SquareGrid(rows, columns)

      grid.gridCells(1)(4).weight = 10

      grid.gridCells(0)(0).linkTo(grid.gridCells(0)(1))
      grid.gridCells(0)(1).linkTo(grid.gridCells(0)(2))
      grid.gridCells(0)(2).linkTo(grid.gridCells(0)(3))
      grid.gridCells(0)(3).linkTo(grid.gridCells(1)(3))
      grid.gridCells(1)(3).linkTo(grid.gridCells(2)(3))
      grid.gridCells(2)(3).linkTo(grid.gridCells(2)(4))
      grid.gridCells(2)(4).linkTo(grid.gridCells(1)(4))
      grid.gridCells(1)(4).linkTo(grid.gridCells(0)(4))
      grid.gridCells(2)(3).linkTo(grid.gridCells(2)(2))
      grid.gridCells(2)(2).linkTo(grid.gridCells(2)(1))
      grid.gridCells(2)(1).linkTo(grid.gridCells(1)(1))
      grid.gridCells(1)(1).linkTo(grid.gridCells(1)(2))
      grid.gridCells(1)(1).linkTo(grid.gridCells(1)(0))
      grid.gridCells(1)(0).linkTo(grid.gridCells(2)(0))
      grid.gridCells(2)(0).linkTo(grid.gridCells(3)(0))
      grid.gridCells(3)(0).linkTo(grid.gridCells(4)(0))
      grid.gridCells(4)(0).linkTo(grid.gridCells(4)(1))
      grid.gridCells(4)(1).linkTo(grid.gridCells(4)(2))
      grid.gridCells(4)(2).linkTo(grid.gridCells(4)(3))
      grid.gridCells(4)(3).linkTo(grid.gridCells(4)(4))
      grid.gridCells(4)(4).linkTo(grid.gridCells(3)(4))
      grid.gridCells(3)(4).linkTo(grid.gridCells(3)(3))
      grid.gridCells(3)(3).linkTo(grid.gridCells(3)(2))
      grid.gridCells(3)(2).linkTo(grid.gridCells(3)(1))

      grid.gridCells(2)(1).linkTo(grid.gridCells(2)(0)) // In order to add a loop

      val maze = Dijkstra.applyAlgorithm(grid)

      println(DijkstraSpec.toString2(maze.asInstanceOf[SquareGrid]))

    }

  }

}

object DijkstraSpec {

  def toString2(squareGrid: SquareGrid): String = {
    val corner      = "+"
    var output      = corner + "----+" * squareGrid.columns.value + "\n"
    var topCumul    = ""
    var bottomCumul = ""

    for (row <- 0 until squareGrid.rows.value; column <- 0 until squareGrid.columns.value) {
      val currentCell  = squareGrid.gridCells(row)(column)
      val southCellOpt = squareGrid.getSouthCellOf(currentCell)
      val eastCellOpt  = squareGrid.getEastCellOf(currentCell)

      val distance =
        if (currentCell.distanceFromStart.toString.length == 1) s"0${currentCell.distanceFromStart}"
        else s"${currentCell.distanceFromStart}"

      var top    = ""
      var bottom = ""
      val body   = s" $distance "

      val eastWall  = if (eastCellOpt.isEmpty || !currentCell.isLinkedTo(eastCellOpt.get)) "|" else " "
      val southWall = if (southCellOpt.isEmpty || !currentCell.isLinkedTo(southCellOpt.get)) "----" else "    "

      top += s"$body$eastWall"
      bottom += s"$southWall$corner"

      topCumul += top
      bottomCumul += bottom

      if (eastCellOpt.isEmpty) {
        output += s"|$topCumul\n+$bottomCumul\n"
        topCumul = ""
        bottomCumul = ""
      }

    }

    output
  }

}
