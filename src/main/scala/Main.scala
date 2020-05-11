import Utils.PositiveInt
import eu.timepit.refined.auto._

object Main extends App {
  val rows: PositiveInt    = 10
  val columns: PositiveInt = 10

  val recursiveBacktrackerMaze = RecursiveBacktrackerMaze(GridType.ORTHOGONAL, rows, columns)

  recursiveBacktrackerMaze.makePng("recursiveBacktrackerMaze")

}
