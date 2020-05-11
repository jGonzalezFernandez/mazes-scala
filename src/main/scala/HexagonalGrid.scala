import Utils.PositiveInt

case class HexagonalGrid(rows: PositiveInt, columns: PositiveInt) extends Grid {

  def getNeighboursOf(cell: Cell): Seq[Cell] = ???

  def makePng(fileName: String): Unit = {
    ???
  }

}
