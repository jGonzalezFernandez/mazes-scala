package mazes

import mazes.Utils.PositiveInt
import mazes.algorithms.GenerationAlgorithm.GenerationAlgorithm
import mazes.algorithms.{GenerationAlgorithm, RecursiveBacktracker}
import mazes.grids.GridType.GridType
import mazes.grids._

trait Maze {

  def gridType: GridType

  def generationAlgorithm: GenerationAlgorithm

  def rows: PositiveInt

  def columnsOpt: Option[PositiveInt]

  private val grid = gridType match {
    case GridType.SQUARE     => SquareGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.HEXAGONAL  => HexagonalGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.TRIANGULAR => TriangularGrid(rows, columnsOpt.getOrElse(rows))
    case GridType.CIRCULAR   => CircularGrid(rows)
  }

  private val maze: Grid = generationAlgorithm match {
    case GenerationAlgorithm.RECURSIVE_BACKTRACKER => RecursiveBacktracker.applyAlgorithm(grid)
  }

  def makePng(fileName: String): Unit = maze.makePng(fileName)

}

/** @param rows used to determine the full size of the grid if columnsOpt is empty
  * @param columnsOpt always ignored when using a circular grid
  */
final case class MazeImpl(gridType: GridType, generationAlgorithm: GenerationAlgorithm, rows: PositiveInt, columnsOpt: Option[PositiveInt] = None) extends Maze
