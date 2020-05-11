import scala.collection.mutable

trait RecursiveBacktrackerAlgo extends Algorithm { // AKA Depth-first search algorithm

  def applyAlgorithm(grid: Grid): Grid = {
    val randomStartingPosition = grid.cellMatrix(grid.getRandomRow)(grid.getRandomColumn)
    val stack                  = mutable.Stack[grid.Cell](randomStartingPosition)
    while (stack.nonEmpty) {
      val currentCell = stack.top
      currentCell.visited = true
      val candidateNeighbours = grid.getNeighboursOf(currentCell).filter(!_.visited)
      val randomCandidateOpt  = grid.getRandomCell(candidateNeighbours)
      randomCandidateOpt match {
        case Some(randomCandidate) =>
          currentCell.linkTo(randomCandidate)
          stack.push(randomCandidate)
        case None => stack.pop()
      }
    }
    grid
  }

}
