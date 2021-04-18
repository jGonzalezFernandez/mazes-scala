import eu.timepit.refined.auto._
import jgonzalezfernandez.mazes.Maze
import jgonzalezfernandez.mazes.algorithms.GenerationAlgorithm
import jgonzalezfernandez.mazes.grids.GridType

object MazesApp {

  def main(args: Array[String]): Unit = {

    val deltaMaze = new Maze(GridType.Triangular, GenerationAlgorithm.RecursiveBacktracker, 25, Some(62))
    val gammaMaze = new Maze(GridType.Square, GenerationAlgorithm.RecursiveDivision, 25, Some(36))
    val sigmaMaze = new Maze(GridType.Hexagonal, GenerationAlgorithm.Random, 17, Some(29))
    val thetaMaze = new Maze(GridType.Circular, GenerationAlgorithm.RecursiveBacktracker, 12)

    deltaMaze.makePng("deltaMaze")
    gammaMaze.makePng("gammaMaze")
    sigmaMaze.makePng("sigmaMaze")
    thetaMaze.makePng("thetaMaze")

  }
}
