package jgonzalezfernandez.mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import jgonzalezfernandez.mazes.Utils._
import jgonzalezfernandez.mazes.grids.TriangularGrid._

final case class TriangularGrid(rows: PositiveInt, columns: PositiveInt) extends RegularTessellation {

  private def isPointingUp(cell: Cell) = isEven(cell.row + cell.column)

  def getNorthCellOf(cell: Cell): Option[Cell] = if (isPointingUp(cell)) None else getCell(cell.row - 1, cell.column)

  def getSouthCellOf(cell: Cell): Option[Cell] = if (!isPointingUp(cell)) None else getCell(cell.row + 1, cell.column)

  def getEastCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column + 1)

  def getWestCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] = Seq(getNorthCellOf(cell), getSouthCellOf(cell), getEastCellOf(cell), getWestCellOf(cell)).flatten

  def makePng(fileName: String): Unit = {
    /*
     *        x1.y0        ||   x0.y1     x2.y1
     *                     ||
     *          c          ||          c
     *                     ||
     *   x0.y1     x2.y1   ||        x1.y0
     */
    val triangleWidth  = EDGE_SIZE
    val halfWidth      = triangleWidth / 2.0
    val triangleHeight = EDGE_SIZE * math.sqrt(3) / 2.0
    val halfHeight     = triangleHeight / 2.0

    // background size and color
    val imgWidth  = (EDGE_SIZE * (columns.value + 1) / 2.0).toInt
    val imgHeight = (triangleHeight * rows.value + 1).toInt
    val canvas    = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val g         = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    allCells.foreach { cell =>
      val cx = halfWidth * cell.column + halfWidth
      val cy = triangleHeight * cell.row + halfHeight

      val x0 = cx - halfWidth
      val x1 = cx
      val x2 = cx + halfWidth
      val y0 = if (isPointingUp(cell)) cy - halfHeight else cy + halfHeight
      val y1 = if (isPointingUp(cell)) cy + halfHeight else cy - halfHeight

      val horizontalWall = new Line2D.Double(x0, y1, x2, y1)
      val eastWall       = new Line2D.Double(x1, y0, x2, y1)
      val westWall       = new Line2D.Double(x0, y1, x1, y0)

      val northCellOpt = getNorthCellOf(cell)
      val southCellOpt = getSouthCellOf(cell)
      val eastCellOpt  = getEastCellOf(cell)
      val westCellOpt  = getWestCellOf(cell)

      if (isPointingUp(cell) && !southCellOpt.exists(cell.isLinkedTo)) g.draw(horizontalWall)
      if (!isPointingUp(cell) && !northCellOpt.exists(cell.isLinkedTo)) g.draw(horizontalWall)
      if (eastCellOpt.isEmpty || !cell.isLinkedTo(eastCellOpt.get)) g.draw(eastWall)
      if (westCellOpt.isEmpty) g.draw(westWall)
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)

  }

}

object TriangularGrid {

  val EDGE_SIZE = 40

}
