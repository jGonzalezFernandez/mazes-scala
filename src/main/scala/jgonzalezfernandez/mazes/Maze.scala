package jgonzalezfernandez.mazes

import jgonzalezfernandez.mazes.Utils.PositiveInt
import jgonzalezfernandez.mazes.algorithms._
import jgonzalezfernandez.mazes.grids._

/** Triangular grid + Sidewinder: discouraged.
  * Triangular grid + BinaryTree: strongly discouraged.
  * RecursiveDivision: only available for the Square grid.
  * @param rows used to determine the full size of the grid if columnsOpt is empty
  * @param columnsOpt always ignored when using a circular grid
  */
final case class Maze(gridType: GridType, generationAlgorithm: GenerationAlgorithm, rows: PositiveInt, columnsOpt: Option[PositiveInt] = None) {

  if (generationAlgorithm == GenerationAlgorithm.RecursiveDivision && gridType != GridType.Square)
    throw new IllegalArgumentException("The RecursiveDivision algorithm only works with Square grids")

  private val grid = gridType match {
    case GridType.Circular   => CircularGrid(rows)
    case GridType.Hexagonal  => HexagonalGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.Square     => SquareGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.Triangular => TriangularGrid(rows, columnsOpt.getOrElse(rows))
  }

  private val maze: Grid = Dijkstra.applyAlgorithm(
    generationAlgorithm match {
      case GenerationAlgorithm.BinaryTree           => BinaryTree.applyAlgorithm(grid)
      case GenerationAlgorithm.RecursiveBacktracker => RecursiveBacktracker.applyAlgorithm(grid)
      case GenerationAlgorithm.RecursiveDivision    => RecursiveDivision.applyAlgorithm(grid.asInstanceOf[SquareGrid])
      case GenerationAlgorithm.Sidewinder           => Sidewinder.applyAlgorithm(grid)
      case GenerationAlgorithm.Random               => ???
    },
    Some(grid.startingCell)
  )
  def max(a: Cell, b: Cell): Cell = if (a.distanceFromStart > b.distanceFromStart) a else b
  maze.allCells.filterNot(_.distanceFromStart == Int.MaxValue).reduceLeft(max).isEnd = true

  def makePng(fileName: String): Unit = maze.makePng(fileName)

}
