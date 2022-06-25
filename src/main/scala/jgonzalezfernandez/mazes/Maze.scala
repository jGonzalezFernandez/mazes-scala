package jgonzalezfernandez.mazes

import eu.timepit.refined.auto._
import jgonzalezfernandez.mazes.Maze._
import jgonzalezfernandez.mazes.Utils.{TwoTo100, randomInt}
import jgonzalezfernandez.mazes.algorithms._
import jgonzalezfernandez.mazes.grids._

import java.io.File

/** Key points:
  *   - Triangular grid + BinaryTree or Sidewinder: not recommended (since unreachable areas will probably be generated), although it can be done.
  *   - RecursiveDivision: currently only available for the Square grid (so it's never chosen when using Random).
  *   - rowsOpt and columnsOpt: default values will be used if they are needed but empty (the Circular grid only needs rows).
  *   - A few loops will be generated randomly, so perfect mazes are unlikely to occur.
  */
final class Maze(gridType: GridType, generationAlgorithm: GenerationAlgorithm, rowsOpt: Option[TwoTo100], columnsOpt: Option[TwoTo100] = None) {

  if (generationAlgorithm == GenerationAlgorithm.RecursiveDivision && gridType != GridType.Square)
    throw new IllegalArgumentException("The RecursiveDivision algorithm only works with Square grids")

  private val grid = gridType match {
    case GridType.Circular   => new CircularGrid(rowsOpt.getOrElse(12))
    case GridType.Hexagonal  => new HexagonalGrid(rowsOpt.getOrElse(17), columnsOpt.getOrElse(29))
    case GridType.Square     => new SquareGrid(rowsOpt.getOrElse(25), columnsOpt.getOrElse(36))
    case GridType.Triangular => new TriangularGrid(rowsOpt.getOrElse(25), columnsOpt.getOrElse(62))
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

  val UNIVERSAL_ALGORITHMS: Seq[Algorithm[Grid]] = Seq(BinaryTree, RecursiveBacktracker, Sidewinder)

}
