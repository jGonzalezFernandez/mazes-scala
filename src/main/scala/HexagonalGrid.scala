import HexagonalGrid._
import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import Utils._

final case class HexagonalGrid(rows: PositiveInt, columns: PositiveInt) extends Grid {

  private def getNorthRow(cell: Cell): Int = if (isEven(cell.column)) cell.row - 1 else cell.row

  private def getSouthRow(cell: Cell): Int = if (isEven(cell.column)) cell.row else cell.row + 1

  def getNorthCellOf(cell: Cell): Option[Cell] = getCell(cell.row - 1, cell.column)

  def getSouthCellOf(cell: Cell): Option[Cell] = getCell(cell.row + 1, cell.column)

  def getNortheastCellOf(cell: Cell): Option[Cell] = getCell(getNorthRow(cell), cell.column + 1)

  def getSoutheastCellOf(cell: Cell): Option[Cell] = getCell(getSouthRow(cell), cell.column + 1)

  def getNorthwestCellOf(cell: Cell): Option[Cell] = getCell(getNorthRow(cell), cell.column - 1)

  def getSouthwestCellOf(cell: Cell): Option[Cell] = getCell(getSouthRow(cell), cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] =
    Seq(getNorthCellOf(cell), getSouthCellOf(cell), getNortheastCellOf(cell), getSoutheastCellOf(cell), getNorthwestCellOf(cell), getSouthwestCellOf(cell)).flatten

  def makePng(fileName: String): Unit = {
    /*             a
     *           <--->
     *   ^     x1y0     x2y0
     * b |
     *   v  x0y1     c     x3y1
     *
     *         x1y2     x2y2
     */
    val a             = EDGE_SIZE / 2.0
    val b             = EDGE_SIZE * math.sqrt(3) / 2.0
    val hexagonHeight = 2 * b

    // background size and color
    val imgWidth  = (3 * a * columns.value + a + 1.5).toInt
    val imgHeight = (hexagonHeight * rows.value + b + 1.5).toInt
    val canvas    = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val g         = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    for (row <- 0 until rows.value; column <- 0 until columns.value) {
      val cx = 3 * a * column + EDGE_SIZE
      var cy = hexagonHeight * row + b
      if (!isEven(column)) cy += b

      val x0 = cx - EDGE_SIZE
      val x1 = cx - a
      val x2 = cx + a
      val x3 = cx + EDGE_SIZE

      val y0 = cy - b
      val y1 = cy
      val y2 = cy + b

      val northWall     = new Line2D.Double(x1, y0, x2, y0)
      val southWall     = new Line2D.Double(x1, y2, x2, y2)
      val northeastWall = new Line2D.Double(x2, y0, x3, y1)
      val southeastWall = new Line2D.Double(x2, y2, x3, y1)
      val northwestWall = new Line2D.Double(x0, y1, x1, y0)
      val southwestWall = new Line2D.Double(x0, y1, x1, y2)

      val currentCell      = cellMatrix(row)(column)
      val northCellOpt     = getNorthCellOf(currentCell)
      val southCellOpt     = getSouthCellOf(currentCell)
      val northeastCellOpt = getNortheastCellOf(currentCell)
      val southeastCellOpt = getSoutheastCellOf(currentCell)
      val northwestCellOpt = getNorthwestCellOf(currentCell)
      val southwestCellOpt = getSouthwestCellOf(currentCell)

      if (northCellOpt.isEmpty || !currentCell.isLinkedTo(northCellOpt.get)) g.draw(northWall)
      if (southCellOpt.isEmpty || !currentCell.isLinkedTo(southCellOpt.get)) g.draw(southWall)
      if (northeastCellOpt.isEmpty || !currentCell.isLinkedTo(northeastCellOpt.get)) g.draw(northeastWall)
      if (southeastCellOpt.isEmpty || !currentCell.isLinkedTo(southeastCellOpt.get)) g.draw(southeastWall)
      if (northwestCellOpt.isEmpty || !currentCell.isLinkedTo(northwestCellOpt.get)) g.draw(northwestWall)
      if (southwestCellOpt.isEmpty || !currentCell.isLinkedTo(southwestCellOpt.get)) g.draw(southwestWall)
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)
  }

}

object HexagonalGrid {

  val EDGE_SIZE = 40

}
