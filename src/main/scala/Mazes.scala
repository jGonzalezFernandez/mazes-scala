import GridType._
import Utils.PositiveInt

trait Maze extends Algorithm {

  def gridType: GridType

  def rows: PositiveInt

  def columns: PositiveInt

  private val grid = gridType match {
    case GridType.ORTHOGONAL => OrthogonalGrid(rows, columns)
    case GridType.HEX        => HexagonalGrid(rows, columns)
  }

  private val maze: Grid = applyAlgorithm(grid)

  def makePng(fileName: String): Unit = maze.makePng(fileName)

}

case class RecursiveBacktrackerMaze(gridType: GridType, rows: PositiveInt, columns: PositiveInt) extends Maze with RecursiveBacktrackerAlgo
