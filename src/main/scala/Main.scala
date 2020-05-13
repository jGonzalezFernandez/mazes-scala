import Utils.PositiveInt
import eu.timepit.refined.auto._

object Main extends App {
  val rows: PositiveInt    = 20
  val columns: PositiveInt = 20

  val recursiveBacktrackerMaze    = RecursiveBacktrackerMaze(GridType.ORTHOGONAL, rows, columns)
  val recursiveBacktrackerHexMaze = RecursiveBacktrackerMaze(GridType.HEX, rows, columns)

  recursiveBacktrackerMaze.makePng("recursiveBacktrackerMaze")
  recursiveBacktrackerHexMaze.makePng("recursiveBacktrackerHexMaze")

}
