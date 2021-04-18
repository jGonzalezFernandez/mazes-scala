package jgonzalezfernandez.mazes

import jgonzalezfernandez.mazes.Maze._
import jgonzalezfernandez.mazes.Utils.{PositiveInt, randomInt}
import jgonzalezfernandez.mazes.algorithms._
import jgonzalezfernandez.mazes.grids._

import java.io.File

/** Triangular grid + BinaryTree or Sidewinder: not recommended (since unreachable areas will probably be generated), although it can be done.
  * RecursiveDivision: currently only available for the Square grid (so it's never chosen when using Random).
  * @param rows used to determine the full size of the grid if columnsOpt is empty.
  * @param columnsOpt always ignored when using a Circular grid.
  * A few loops will be generated randomly, so perfect mazes are unlikely to occur.
  */
final class Maze(gridType: GridType, generationAlgorithm: GenerationAlgorithm, rows: PositiveInt, columnsOpt: Option[PositiveInt] = None) {

  if (generationAlgorithm == GenerationAlgorithm.RecursiveDivision && gridType != GridType.Square)
    throw new IllegalArgumentException("The RecursiveDivision algorithm only works with Square grids")

  private val grid = gridType match {
    case GridType.Circular   => new CircularGrid(rows)
    case GridType.Hexagonal  => new HexagonalGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.Square     => new SquareGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.Triangular => new TriangularGrid(rows, columnsOpt.getOrElse(rows))
  }

  private def addLoops(grid: Grid): Grid = {

    def randomlyAddLoop(cell: Cell): Boolean = cell.linkCount == 1 && randomInt(9) == 0

    grid.allCells.foreach { cell =>
      if (cell.linkCount == 0 || randomlyAddLoop(cell)) { // the count could be 0 when using Triangular grid + Sidewinder
        val unlinkedNeighbours = grid.getNeighboursOf(cell).filterNot(cell.isLinkedTo)
        val candidateOpt       = unlinkedNeighbours.sortBy(_.linkCount).headOption // mainly for aesthetic reasons
        candidateOpt.foreach(cell.linkTo(_))
      }
    }

    grid
  }

  private val maze: Grid = Dijkstra.applyAlgorithm(
    addLoops(
      generationAlgorithm match {
        case GenerationAlgorithm.BinaryTree           => BinaryTree.applyAlgorithm(grid)
        case GenerationAlgorithm.RecursiveBacktracker => RecursiveBacktracker.applyAlgorithm(grid)
        case GenerationAlgorithm.RecursiveDivision    => RecursiveDivision.applyAlgorithm(grid.asInstanceOf[SquareGrid])
        case GenerationAlgorithm.Sidewinder           => Sidewinder.applyAlgorithm(grid)
        case GenerationAlgorithm.Random               => UNIVERSAL_ALGORITHMS(randomInt(UNIVERSAL_ALGORITHMS.length)).applyAlgorithm(grid)
      }
    ),
    Some(grid.startingCell)
  )
  private def max(a: Cell, b: Cell): Cell = if (a.distanceFromStart > b.distanceFromStart) a else b
  maze.allCells.filterNot(_.distanceFromStart == Int.MaxValue).reduceLeft(max).isEnd = true

  def makePng(fileName: String): File = maze.makePng(fileName)

}

object Maze {

  val UNIVERSAL_ALGORITHMS = Seq(BinaryTree, RecursiveBacktracker, Sidewinder)

}
