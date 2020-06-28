package jgonzalezfernandez.mazes.grids

import enumeratum._

sealed trait GridType extends EnumEntry

object GridType extends Enum[GridType] {

  val values: IndexedSeq[GridType] = findValues

  case object Circular   extends GridType
  case object Hexagonal  extends GridType
  case object Square     extends GridType
  case object Triangular extends GridType

}
