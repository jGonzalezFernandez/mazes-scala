package mazes

import mazes.Utils.PositiveInt
import mazes.algorithms.{Algorithm, RecursiveBacktrackerAlgo}
import mazes.grids.GridType.GridType
import mazes.grids._

trait Maze extends Algorithm {

  def gridType: GridType

  def rows: PositiveInt

  def columns: PositiveInt

  private val grid = gridType match {
    case GridType.SQUARE     => SquareGrid(rows, columns)
    case GridType.HEXAGONAL  => HexagonalGrid(rows, columns)
    case GridType.TRIANGULAR => TriangularGrid(rows, columns)
    case GridType.CIRCULAR   => CircularGrid(rows)
  }

  private val maze: Grid = applyAlgorithm(grid)

  def makePng(fileName: String): Unit = maze.makePng(fileName)

}

final case class RecursiveBacktrackerMaze(gridType: GridType, rows: PositiveInt, columns: PositiveInt) extends Maze with RecursiveBacktrackerAlgo
// Hay que poder pasar no sólo un GridType, sino también un algoritmo?
// Añadir los otros dos algoritmos si eres capaz, del snapshot del email...
// reorganiza los imports para usar lo menos posible, salvo en los companion objects