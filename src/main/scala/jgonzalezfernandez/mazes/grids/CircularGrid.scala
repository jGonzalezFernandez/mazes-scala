package jgonzalezfernandez.mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import jgonzalezfernandez.mazes.Utils.PositiveInt
import jgonzalezfernandez.mazes.grids.CircularGrid._

import scala.collection.mutable.ArrayBuffer

final case class CircularGrid(rows: PositiveInt) extends Grid { // AKA polar grid

  /* In a polar grid, each row is delimited by a pair of concentric circles. Therefore, if we were to use a matrix to
   * represent the different cells of the maze, the outer ones would have to be much wider than the inner ones.
   * But we want each cell to be about the same height and width.
   * Hence, we need to build a jagged array and put more cells in the outer rows than in the inner rows.
   */
  val indexedCells: Vector[ArrayBuffer[Cell]] = {
    val rowHeight   = 1.0 // true value doesn't matter. In this context we only care about the proportions
    val indexedRows = Vector.fill(rows.value)(ArrayBuffer.empty[Cell])
    indexedRows(0) += Cell(0, 0)

    for (row <- 1 until rows.value) {
      // If the width of the cells in the previous row were too large for the current row (ratio > 1), we will decrease
      // it by inserting more cells or columns than before (usually double, except for the row at index 1).
      val innerRowRadius     = rowHeight * row
      val innerCircumference = 2 * Math.PI * innerRowRadius
      val previousCellCount  = indexedRows(row - 1).length
      val previousCellWidth  = innerCircumference / previousCellCount
      val ratio              = Math.ceil(previousCellWidth / rowHeight).toInt
      val columns            = previousCellCount * ratio
      indexedRows(row) ++= ArrayBuffer.tabulate[Cell](columns)(column => Cell(row, column))
    }

    indexedRows
  }

  val startingCell: Cell = indexedCells(0)(0)

  def getInwardCellOf(cell: Cell): Option[Cell] = {
    indexedCells.lift(cell.row - 1).map(previousRow => indexedCells(cell.row).length / previousRow.length) match {
      case Some(ratio) => getCell(cell.row - 1, cell.column / ratio)
      case _           => None
    }
  }

  def getOutwardsCellsOf(cell: Cell): Seq[Cell] = {
    if (cell.row == 0 & cell.column == 0) indexedCells(1).toSeq
    else
      indexedCells.lift(cell.row + 1).map(_.length / indexedCells(cell.row).length) match {
        case None                      => Seq()
        case Some(ratio) if ratio == 2 => Seq(getCell(cell.row + 1, cell.column * ratio), getCell(cell.row + 1, cell.column * ratio + 1)).flatten
        case _                         => Seq(getCell(cell.row + 1, cell.column)).flatten // ratio == 1
      }
  }

  def getClockwiseCellOf(cell: Cell): Option[Cell] = // Circular grids have no lateral boundaries
    if (cell.column == indexedCells(cell.row).length - 1) getCell(cell.row, 0) else getCell(cell.row, cell.column + 1)

  def getCounterClockwiseCellOf(cell: Cell): Option[Cell] =
    if (cell.column == 0) getCell(cell.row, indexedCells(cell.row).length - 1) else getCell(cell.row, cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] =
    getOutwardsCellsOf(cell) ++ Seq(getInwardCellOf(cell), getClockwiseCellOf(cell), getCounterClockwiseCellOf(cell)).flatten

  def makePng(fileName: String): Unit = {
    /*
     *                     outerCCW
     *
     *         innerCCW
     *
     *   c     innerCW     outerCW
     */
    val c = EDGE_SIZE * indexedCells.length

    // background size and color
    val imgSize = 2 * c + 1
    val canvas  = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB)
    val g       = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillOval(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    allCells.foreach { cell =>
      if (cell.row != 0 && cell.column != 0) { // Since the central cell has only outward neighbors, we skip it to avoid drawing its lateral boundary
        val innerRowRadius = EDGE_SIZE * cell.row
        val outerRowRadius = EDGE_SIZE * (cell.row + 1)
        val theta          = 2 * Math.PI / indexedCells(cell.row).length // cell angle size
        val thetaCCW       = theta * cell.column
        val thetaCW        = theta * (cell.column + 1)

        // Perhaps we could work directly on the polar coordinate system using the angles and radii, but we are going to
        // calculate the Cartesian coordinates of the different points using trigonometry.
        val innerCCWx = c + (innerRowRadius * Math.cos(thetaCCW))
        val innerCCWy = c + (innerRowRadius * Math.sin(thetaCCW))
        val innerCWx  = c + (innerRowRadius * Math.cos(thetaCW))
        val innerCWy  = c + (innerRowRadius * Math.sin(thetaCW))
        val outerCCWx = c + (outerRowRadius * Math.cos(thetaCCW))
        val outerCCWy = c + (outerRowRadius * Math.sin(thetaCCW))
        val outerCWx  = c + (outerRowRadius * Math.cos(thetaCW))
        val outerCWy  = c + (outerRowRadius * Math.sin(thetaCW))

        val inwardWall    = new Line2D.Double(innerCCWx, innerCCWy, innerCWx, innerCWy)
        val outwardWall   = new Line2D.Double(outerCCWx, outerCCWy, outerCWx, outerCWy)
        val clockwiseWall = new Line2D.Double(innerCWx, innerCWy, outerCWx, outerCWy)

        val inwardCelOpt = getInwardCellOf(cell)
        val outwardCells = getOutwardsCellsOf(cell)
        val clockwiseOpt = getClockwiseCellOf(cell)

        if (inwardCelOpt.isEmpty || !cell.isLinkedTo(inwardCelOpt.get)) g.draw(inwardWall)
        if (clockwiseOpt.isEmpty || !cell.isLinkedTo(clockwiseOpt.get)) g.draw(clockwiseWall)
        if (outwardCells.isEmpty) g.draw(outwardWall)

        if (cell == startingCell || cell.isEnd) {
          g.draw(new Line2D.Double(innerCCWx, innerCCWy, outerCWx, outerCWy))
          g.draw(new Line2D.Double(outerCCWx, outerCCWy, innerCWx, innerCWy))
        }

      }
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)
  }

}

object CircularGrid {

  val EDGE_SIZE = 40

}
