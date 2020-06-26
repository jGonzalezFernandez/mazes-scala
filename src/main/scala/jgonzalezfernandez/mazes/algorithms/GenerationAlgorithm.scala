package jgonzalezfernandez.mazes.algorithms

import enumeratum._

sealed trait GenerationAlgorithm extends EnumEntry

object GenerationAlgorithm extends Enum[GenerationAlgorithm] {

  val values: IndexedSeq[GenerationAlgorithm] = findValues

  case object BinaryTree           extends GenerationAlgorithm
  case object RecursiveBacktracker extends GenerationAlgorithm
  case object RecursiveDivision    extends GenerationAlgorithm
  case object Sidewinder           extends GenerationAlgorithm
  case object Random               extends GenerationAlgorithm

}
