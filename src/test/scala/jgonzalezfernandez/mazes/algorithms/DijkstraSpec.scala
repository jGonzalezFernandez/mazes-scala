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
      val squareGrid = SquareGrid(rows, columns)

      squareGrid.indexedCells(1)(4).weight = 10

      squareGrid.indexedCells(0)(0).linkTo(squareGrid.indexedCells(0)(1))
      squareGrid.indexedCells(0)(1).linkTo(squareGrid.indexedCells(0)(2))
      squareGrid.indexedCells(0)(2).linkTo(squareGrid.indexedCells(0)(3))
      squareGrid.indexedCells(0)(3).linkTo(squareGrid.indexedCells(1)(3))
      squareGrid.indexedCells(1)(3).linkTo(squareGrid.indexedCells(2)(3))
      squareGrid.indexedCells(2)(3).linkTo(squareGrid.indexedCells(2)(4))
      squareGrid.indexedCells(2)(4).linkTo(squareGrid.indexedCells(1)(4))
      squareGrid.indexedCells(1)(4).linkTo(squareGrid.indexedCells(0)(4))
      squareGrid.indexedCells(2)(3).linkTo(squareGrid.indexedCells(2)(2))
      squareGrid.indexedCells(2)(2).linkTo(squareGrid.indexedCells(2)(1))
      squareGrid.indexedCells(2)(1).linkTo(squareGrid.indexedCells(1)(1))
      squareGrid.indexedCells(1)(1).linkTo(squareGrid.indexedCells(1)(2))
      squareGrid.indexedCells(1)(1).linkTo(squareGrid.indexedCells(1)(0))
      squareGrid.indexedCells(1)(0).linkTo(squareGrid.indexedCells(2)(0))
      squareGrid.indexedCells(2)(0).linkTo(squareGrid.indexedCells(2)(1)) // to open a loop
      squareGrid.indexedCells(2)(0).linkTo(squareGrid.indexedCells(3)(0))
      squareGrid.indexedCells(3)(0).linkTo(squareGrid.indexedCells(4)(0))
      squareGrid.indexedCells(4)(0).linkTo(squareGrid.indexedCells(4)(1))
      squareGrid.indexedCells(4)(1).linkTo(squareGrid.indexedCells(4)(2))
      squareGrid.indexedCells(4)(2).linkTo(squareGrid.indexedCells(4)(3))
      squareGrid.indexedCells(4)(3).linkTo(squareGrid.indexedCells(4)(4))
      squareGrid.indexedCells(4)(4).linkTo(squareGrid.indexedCells(3)(4))
      squareGrid.indexedCells(3)(4).linkTo(squareGrid.indexedCells(3)(3))
      squareGrid.indexedCells(3)(3).linkTo(squareGrid.indexedCells(3)(2))
      squareGrid.indexedCells(3)(2).linkTo(squareGrid.indexedCells(3)(1))

      Dijkstra.applyAlgorithm(squareGrid, Some(squareGrid.indexedCells(0)(0)))

      assert(squareGrid.indexedCells(0)(0).distanceFromStart == 0)
      assert(squareGrid.indexedCells(0)(1).distanceFromStart == 1)
      assert(squareGrid.indexedCells(0)(2).distanceFromStart == 2)
      assert(squareGrid.indexedCells(0)(3).distanceFromStart == 3)
      assert(squareGrid.indexedCells(0)(4).distanceFromStart == 17)
      assert(squareGrid.indexedCells(1)(0).distanceFromStart == 9)
      assert(squareGrid.indexedCells(1)(1).distanceFromStart == 8)
      assert(squareGrid.indexedCells(1)(2).distanceFromStart == 9)
      assert(squareGrid.indexedCells(1)(3).distanceFromStart == 4)
      assert(squareGrid.indexedCells(1)(4).distanceFromStart == 16)
      assert(squareGrid.indexedCells(2)(0).distanceFromStart == 8)
      assert(squareGrid.indexedCells(2)(1).distanceFromStart == 7)
      assert(squareGrid.indexedCells(2)(2).distanceFromStart == 6)
      assert(squareGrid.indexedCells(2)(3).distanceFromStart == 5)
      assert(squareGrid.indexedCells(2)(4).distanceFromStart == 6)
      assert(squareGrid.indexedCells(3)(0).distanceFromStart == 9)
      assert(squareGrid.indexedCells(3)(1).distanceFromStart == 18)
      assert(squareGrid.indexedCells(3)(2).distanceFromStart == 17)
      assert(squareGrid.indexedCells(3)(3).distanceFromStart == 16)
      assert(squareGrid.indexedCells(3)(4).distanceFromStart == 15)
      assert(squareGrid.indexedCells(4)(0).distanceFromStart == 10)
      assert(squareGrid.indexedCells(4)(1).distanceFromStart == 11)
      assert(squareGrid.indexedCells(4)(2).distanceFromStart == 12)
      assert(squareGrid.indexedCells(4)(3).distanceFromStart == 13)
      assert(squareGrid.indexedCells(4)(4).distanceFromStart == 14)

      println(squareGrid.toString(showDistances = true))

    }

  }

}
