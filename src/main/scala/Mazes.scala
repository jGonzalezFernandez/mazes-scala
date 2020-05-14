import GridType._
import Utils.PositiveInt

trait Maze extends Algorithm {

  def gridType: GridType

  def rows: PositiveInt

  def columns: PositiveInt

  private val grid = gridType match {
    case GridType.SQUARE     => SquareGrid(rows, columns)
    case GridType.HEXAGONAL  => HexagonalGrid(rows, columns)
    case GridType.TRIANGULAR => TriangularGrid(rows, columns)
  }

  private val maze: Grid = applyAlgorithm(grid)

  def makePng(fileName: String): Unit = maze.makePng(fileName)

}

final case class RecursiveBacktrackerMaze(gridType: GridType, rows: PositiveInt, columns: PositiveInt) extends Maze with RecursiveBacktrackerAlgo
