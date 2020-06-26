import eu.timepit.refined.auto._
import jgonzalezfernandez.mazes.Maze
import jgonzalezfernandez.mazes.Utils.PositiveInt
import jgonzalezfernandez.mazes.algorithms.GenerationAlgorithm
import jgonzalezfernandez.mazes.grids.GridType

object MazesApp {

  def main(args: Array[String]): Unit = {
    val rows: PositiveInt    = 13
    val columns: PositiveInt = 13

    val deltaMaze = Maze(GridType.Triangular, GenerationAlgorithm.RecursiveBacktracker, rows, Some(columns))
    val gammaMaze = Maze(GridType.Square, GenerationAlgorithm.RecursiveDivision, rows, Some(columns))
    val sigmaMaze = Maze(GridType.Hexagonal, GenerationAlgorithm.Sidewinder, rows, Some(columns))
    val thetaMaze = Maze(GridType.Circular, GenerationAlgorithm.BinaryTree, rows)

    deltaMaze.makePng("deltaMaze")
    gammaMaze.makePng("gammaMaze")
    sigmaMaze.makePng("sigmaMaze")
    thetaMaze.makePng("thetaMaze")
  }
}
