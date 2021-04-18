package jgonzalezfernandez.mazes.grids

import jgonzalezfernandez.mazes.Utils._
import jgonzalezfernandez.mazes.grids.HexagonalGrid._

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import java.io.File

final class HexagonalGrid(val rows: PositiveInt, val columns: PositiveInt) extends RegularTessellation {

  private def getNorthRow(cell: Cell): Int = if (isEven(cell.column)) cell.row - 1 else cell.row

  private def getSouthRow(cell: Cell): Int = if (isEven(cell.column)) cell.row else cell.row + 1

  def getNortheastCellOf(cell: Cell): Option[Cell] = getCell(getNorthRow(cell), cell.column + 1)

  def getSoutheastCellOf(cell: Cell): Option[Cell] = getCell(getSouthRow(cell), cell.column + 1)

  def getNorthwestCellOf(cell: Cell): Option[Cell] = getCell(getNorthRow(cell), cell.column - 1)

  def getSouthwestCellOf(cell: Cell): Option[Cell] = getCell(getSouthRow(cell), cell.column - 1)

  override def getNeighboursOf(cell: Cell): Seq[Cell] =
    Seq(getNorthCellOf(cell), getSouthCellOf(cell), getNortheastCellOf(cell), getSoutheastCellOf(cell), getNorthwestCellOf(cell), getSouthwestCellOf(cell)).flatten

  def makePng(fileName: String): File = {
    /*
     *      x1.y0     x2.y0
     *
     *   x0.y1     c     x3.y1
     *
     *      x1.y2     x2.y2
     */
    val hexagonWidth  = EDGE_SIZE * 2.0
    val aQuarterWidth = hexagonWidth / 4.0
    val hexagonHeight = EDGE_SIZE * math.sqrt(3)
    val halfHeight    = hexagonHeight / 2.0

    // background size and color
    val imgWidth  = Math.ceil(3 * aQuarterWidth * columns.value + aQuarterWidth + 1.5).toInt
    val imgHeight = Math.ceil(hexagonHeight * rows.value + halfHeight + 1.5).toInt
    val canvas    = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val g         = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    allCells.foreach { cell =>
      val cx = 3 * aQuarterWidth * cell.column + EDGE_SIZE
      var cy = hexagonHeight * cell.row + halfHeight
      if (!isEven(cell.column)) cy += halfHeight

      val x0 = cx - EDGE_SIZE
      val x1 = cx - aQuarterWidth
      val x2 = cx + aQuarterWidth
      val x3 = cx + EDGE_SIZE

      val y0 = cy - halfHeight
      val y1 = cy
      val y2 = cy + halfHeight

      val northWall     = new Line2D.Double(x1, y0, x2, y0)
      val southWall     = new Line2D.Double(x1, y2, x2, y2)
      val northeastWall = new Line2D.Double(x2, y0, x3, y1)
      val southeastWall = new Line2D.Double(x2, y2, x3, y1)
      val northwestWall = new Line2D.Double(x0, y1, x1, y0)
      val southwestWall = new Line2D.Double(x0, y1, x1, y2)

      val northCellOpt     = getNorthCellOf(cell)
      val southCellOpt     = getSouthCellOf(cell)
      val northeastCellOpt = getNortheastCellOf(cell)
      val southeastCellOpt = getSoutheastCellOf(cell)
      val northwestCellOpt = getNorthwestCellOf(cell)
      val southwestCellOpt = getSouthwestCellOf(cell)

      if (northCellOpt.isEmpty || !cell.isLinkedTo(northCellOpt.get)) g.draw(northWall)
      if (northeastCellOpt.isEmpty || !cell.isLinkedTo(northeastCellOpt.get)) g.draw(northeastWall)
      if (southeastCellOpt.isEmpty || !cell.isLinkedTo(southeastCellOpt.get)) g.draw(southeastWall)
      if (southCellOpt.isEmpty) g.draw(southWall)
      if (northwestCellOpt.isEmpty) g.draw(northwestWall)
      if (southwestCellOpt.isEmpty) g.draw(southwestWall)

      if (cell == startingCell || cell.isEnd) Grid.drawPoint(g, cx, cy)
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)
  }

}

object HexagonalGrid {

  val EDGE_SIZE = 40

}
