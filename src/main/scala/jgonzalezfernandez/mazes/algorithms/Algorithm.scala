package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.{Cell, Grid}

trait Algorithm {

  def applyAlgorithm(grid: Grid, startingPositionOpt: Option[Cell] = None): Grid

}
