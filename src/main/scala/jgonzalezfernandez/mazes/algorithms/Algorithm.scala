package jgonzalezfernandez.mazes.algorithms

import jgonzalezfernandez.mazes.grids.Grid

trait Algorithm {

  def applyAlgorithm(grid: Grid): Grid

}
