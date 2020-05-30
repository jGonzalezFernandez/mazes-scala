package mazes.algorithms

import mazes.grids.Grid

trait Algorithm {

  def applyAlgorithm(grid: Grid): Grid

}
