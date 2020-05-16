import Utils.PositiveInt
import eu.timepit.refined.auto._

object Main extends App {
  val rows: PositiveInt    = 20
  val columns: PositiveInt = 20

  val regularMaze = RecursiveBacktrackerMaze(GridType.SQUARE, rows, columns)
  val sigmaMaze   = RecursiveBacktrackerMaze(GridType.HEXAGONAL, rows, columns)
  val deltaMaze   = RecursiveBacktrackerMaze(GridType.TRIANGULAR, rows, columns)

  regularMaze.makePng("regularMaze")
  sigmaMaze.makePng("sigmaMaze")
  deltaMaze.makePng("deltaMaze")
}
