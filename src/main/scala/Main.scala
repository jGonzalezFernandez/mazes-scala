import eu.timepit.refined.auto._
import mazes.MazeImpl
import mazes.Utils.PositiveInt
import mazes.algorithms.GenerationAlgorithm
import mazes.grids.GridType

object Main extends App {
  val rows: PositiveInt    = 10
  val columns: PositiveInt = 10

  val gammaMaze = MazeImpl(GridType.SQUARE, GenerationAlgorithm.RECURSIVE_BACKTRACKER, rows, Some(columns))
  val sigmaMaze = MazeImpl(GridType.HEXAGONAL, GenerationAlgorithm.RECURSIVE_BACKTRACKER, rows, Some(columns))
  val deltaMaze = MazeImpl(GridType.TRIANGULAR, GenerationAlgorithm.RECURSIVE_BACKTRACKER, rows, Some(columns))
  val thetaMaze = MazeImpl(GridType.CIRCULAR, GenerationAlgorithm.RECURSIVE_BACKTRACKER, rows)

  gammaMaze.makePng("gammaMaze")
  sigmaMaze.makePng("sigmaMaze")
  deltaMaze.makePng("deltaMaze")
  thetaMaze.makePng("thetaMaze")
}
