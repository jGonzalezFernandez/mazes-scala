import eu.timepit.refined.auto._
import mazes.RecursiveBacktrackerMaze
import mazes.Utils.PositiveInt
import mazes.grids.GridType

object Main extends App {
  val rows: PositiveInt    = 10
  val columns: PositiveInt = 10

//  val regularMaze = RecursiveBacktrackerMaze(GridType.SQUARE, rows, columns)
//  val sigmaMaze    = RecursiveBacktrackerMaze(GridType.HEXAGONAL, rows, columns)
//  val deltaMaze    = RecursiveBacktrackerMaze(GridType.TRIANGULAR, rows, columns)
  val circularMaze = RecursiveBacktrackerMaze(GridType.CIRCULAR, rows, columns)

//  regularMaze.makePng("regularMaze")
//  sigmaMaze.makePng("sigmaMaze")
//  deltaMaze.makePng("deltaMaze")
  circularMaze.makePng("circularMaze")
}
