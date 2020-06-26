package jgonzalezfernandez.mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import jgonzalezfernandez.mazes.Utils._
import jgonzalezfernandez.mazes.grids.SquareGrid._

final case class SquareGrid(rows: PositiveInt, columns: PositiveInt) extends RegularTessellation { // AKA Orthogonal grid

  def toString(showDistances: Boolean): String = {
    val corner         = "+"
    val horizontalWall = "----"
    val fourSpaces     = "    "
    val verticalWall   = "|"
    val space          = " "

    var output    = corner + s"$horizontalWall$corner" * columns.value + "\n"
    var topRow    = ""
    var bottomRow = ""

    for (row <- 0 until rows.value; column <- 0 until columns.value) {
      val cell         = indexedCells(row)(column)
      val southCellOpt = getSouthCellOf(cell)
      val eastCellOpt  = getEastCellOf(cell)

      lazy val distance = "%02d".format(cell.distanceFromStart)
      val body          = if (showDistances) s"$space$distance$space" else fourSpaces
      val eastWall      = if (eastCellOpt.isEmpty || !cell.isLinkedTo(eastCellOpt.get)) verticalWall else space
      val southWall     = if (southCellOpt.isEmpty || !cell.isLinkedTo(southCellOpt.get)) horizontalWall else fourSpaces

      topRow += s"$body$eastWall"
      bottomRow += s"$southWall$corner"

      if (eastCellOpt.isEmpty) { // Once we reach the end of the row, we assemble it and add it to the output
        output += s"$verticalWall$topRow\n$corner$bottomRow\n"
        topRow = ""
        bottomRow = ""
      }
    }

    output
  }

  def makePng(fileName: String): Unit = {
    /*
     *   x0.y0     x1.y0
     *
     *          c
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
    allCells.foreach { cell =>
      val x0 = EDGE_SIZE * cell.column
      val y0 = EDGE_SIZE * cell.row
      val x1 = EDGE_SIZE * (cell.column + 1)
      val y1 = EDGE_SIZE * (cell.row + 1)

      val northWall = new Line2D.Double(x0, y0, x1, y0)
      val southWall = new Line2D.Double(x0, y1, x1, y1)
      val eastWall  = new Line2D.Double(x1, y0, x1, y1)
      val westWall  = new Line2D.Double(x0, y0, x0, y1)

      val northCellOpt = getNorthCellOf(cell)
      val southCellOpt = getSouthCellOf(cell)
      val eastCellOpt  = getEastCellOf(cell)
      val westCellOpt  = getWestCellOf(cell)

      // Since cells share walls, we don't need to draw all of them for each one:
      if (northCellOpt.isEmpty || !cell.isLinkedTo(northCellOpt.get)) g.draw(northWall)
      if (eastCellOpt.isEmpty || !cell.isLinkedTo(eastCellOpt.get)) g.draw(eastWall)
      // But then we must not forget the missing outer walls (in this case, the southern and western boundaries):
      if (southCellOpt.isEmpty) g.draw(southWall)
      if (westCellOpt.isEmpty) g.draw(westWall)

      if (cell == startingCell || cell.isEnd) Grid.drawPoint(g, midpoint(x0, x1), midpoint(y0, y1))
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)
  }

}

object SquareGrid {

  val EDGE_SIZE = 40

}
