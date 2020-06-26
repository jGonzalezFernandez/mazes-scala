package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

trait Algorithm[T <: Grid] {

  def applyAlgorithm(grid: T, startingPositionOpt: Option[Cell] = None): Grid

}
