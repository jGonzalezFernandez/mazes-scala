import eu.timepit.refined.refineV
import jgonzalezfernandez.mazes.Maze
import jgonzalezfernandez.mazes.Utils.PositiveInt
import jgonzalezfernandez.mazes.algorithms.GenerationAlgorithm
import jgonzalezfernandez.mazes.grids.GridType

import scala.annotation.tailrec
import scala.collection.immutable.ListMap
import scala.io.StdIn.{readInt, readLine}
import scala.util.{Success, Try}

object MazesApp {

  def main(args: Array[String]): Unit = {

    val gridType = getInput("Select grid type:", toMap(GridType.values))

    val customize = readLine("Do you want to customize the maze? Yes / No (random algo with default size)%n").toLowerCase

    val (algorithm, rowsOpt, columnsOpt) = {
      if (customize == "y" || customize == "yes") {
        (
          gridType match {
            case GridType.Square => getInput("Select algorithm:", toMap(GenerationAlgorithm.values))
            case _               => getInput("Select algorithm:", toMap(GenerationAlgorithm.values.filterNot(_ == GenerationAlgorithm.RecursiveDivision)))
          },
          gridType match {
            case GridType.Circular => Some(getInput("Select radius size (must be more than 0)"))
            case _                 => Some(getInput("Select number of rows (must be more than 0)"))
          },
          gridType match {
            case GridType.Circular => None
            case _                 => Some(getInput("Select number of columns (must be more than 0)"))
          }
        )
      }
      else (GenerationAlgorithm.Random, None, None)
    }

    new Maze(gridType, algorithm, rowsOpt, columnsOpt).makePng("yourMaze")
    println("Done. You can find your maze inside the PNGs folder")

  }

  def toMap[T](seq: Seq[T]): Map[Int, T] = seq.zipWithIndex.map(_.swap).to(ListMap)

  @tailrec
  def getInput[T](message: String, allowedValues: Map[Int, T]): T = {
    println(s"$message ${allowedValues.mkString(", ")}")
    safeReadInt match {
      case Success(int) if allowedValues.contains(int) => allowedValues(int)
      case _                                           => getInput(message, allowedValues)
    }
  }

  @tailrec
  def getInput(message: String): PositiveInt = {
    println(message)
    readPositiveInt match {
      case Right(positiveInt) => positiveInt
      case _                  => getInput(message)
    }
  }

  def safeReadInt: Try[Int]                   = Try(readInt())
  def readPositiveInt: Either[_, PositiveInt] = safeReadInt.toEither.flatMap(refineV(_))

}
