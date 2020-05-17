import eu.timepit.refined.auto._
import mazes.RecursiveBacktrackerMaze
import mazes.Utils.PositiveInt
import mazes.grids.GridType

object Main extends App {
  val rows: PositiveInt    = 20
  val columns: PositiveInt = 20

  val regularMaze = RecursiveBacktrackerMaze(GridType.SQUARE, rows, columns)
  val sigmaMaze   = mazes.RecursiveBacktrackerMaze(GridType.HEXAGONAL, rows, columns)
  val deltaMaze   = mazes.RecursiveBacktrackerMaze(GridType.TRIANGULAR, rows, columns)

  regularMaze.makePng("regularMaze")
  sigmaMaze.makePng("sigmaMaze")
  deltaMaze.makePng("deltaMaze")
}
