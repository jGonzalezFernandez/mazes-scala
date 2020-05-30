package mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import mazes.Utils.PositiveInt
import mazes.grids.SquareGrid._

final case class SquareGrid(rows: PositiveInt, columns: PositiveInt) extends RegularTessellation { // AKA Orthogonal grid

  def getNorthCellOf(cell: Cell): Option[Cell] = getCell(cell.row - 1, cell.column)

  def getSouthCellOf(cell: Cell): Option[Cell] = getCell(cell.row + 1, cell.column)

  def getEastCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column + 1)

  def getWestCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] = Seq(getNorthCellOf(cell), getSouthCellOf(cell), getEastCellOf(cell), getWestCellOf(cell)).flatten

  def makePng(fileName: String): Unit = {
    /*
     *   x0.y0     x1.y0
     *
     *
     *
     *   x0.y1     x1.y1
     */

    // background size and color
    val imgWidth  = EDGE_SIZE * columns.value + 1
    val imgHeight = EDGE_SIZE * rows.value + 1
    val canvas    = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val g         = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    for (row <- 0 until rows.value; column <- 0 until columns.value) {
      val x0 = EDGE_SIZE * column
      val y0 = EDGE_SIZE * row
      val x1 = EDGE_SIZE * (column + 1)
      val y1 = EDGE_SIZE * (row + 1)

      val northWall = new Line2D.Double(x0, y0, x1, y0)
      val southWall = new Line2D.Double(x0, y1, x1, y1)
      val eastWall  = new Line2D.Double(x1, y0, x1, y1)
      val westWall  = new Line2D.Double(x0, y0, x0, y1)

      val currentCell  = gridCells(row)(column)
      val northCellOpt = getNorthCellOf(currentCell)
      val southCellOpt = getSouthCellOf(currentCell)
      val eastCellOpt  = getEastCellOf(currentCell)
      val westCellOpt  = getWestCellOf(currentCell)

      // Since cells share walls, we don't need to draw all of them for each one:
      if (northCellOpt.isEmpty || !currentCell.isLinkedTo(northCellOpt.get)) g.draw(northWall)
      if (eastCellOpt.isEmpty || !currentCell.isLinkedTo(eastCellOpt.get)) g.draw(eastWall)
      // But then we must not forget the missing outer walls (in this case, the southern and western boundaries):
      if (southCellOpt.isEmpty) g.draw(southWall)
      if (westCellOpt.isEmpty) g.draw(westWall)
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)
  }

}

object SquareGrid {

  val EDGE_SIZE = 40

}
