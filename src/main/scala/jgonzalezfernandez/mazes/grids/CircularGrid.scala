package jgonzalezfernandez.mazes.grids

import jgonzalezfernandez.mazes.Utils._
import jgonzalezfernandez.mazes.grids.CircularGrid._

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import java.io.File
import scala.collection.mutable.ArrayBuffer

final class CircularGrid(val rows: TwoTo100) extends Grid { // AKA Polar grid

  /* In a Polar grid, each row is delimited by a pair of concentric circles. Therefore, if we were to use a matrix to
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
        case _                         => getCell(cell.row + 1, cell.column).toSeq // ratio == 1
      }
  }

  def getClockwiseCellOf(cell: Cell): Option[Cell] = // Circular grids have no lateral boundaries
    if (cell.column == indexedCells(cell.row).length - 1) getCell(cell.row, 0) else getCell(cell.row, cell.column + 1)

  def getCounterClockwiseCellOf(cell: Cell): Option[Cell] =
    if (cell.column == 0) getCell(cell.row, indexedCells(cell.row).length - 1) else getCell(cell.row, cell.column - 1)

  override def getNeighboursOf(cell: Cell): Seq[Cell] =
    getOutwardsCellsOf(cell) ++ Seq(getInwardCellOf(cell), getClockwiseCellOf(cell), getCounterClockwiseCellOf(cell)).flatten

  // Required by the Binary Tree and Sidewinder algorithms:

  override def getNorthCellOf(cell: Cell): Option[Cell] = getInwardCellOf(cell)

  override def getEastCellOf(cell: Cell): Option[Cell] = getClockwiseCellOf(cell)

  def makePng(fileName: String): File = {
    /*
     *                          outerCCW
     *
     *              innerCCW
     *                       c
     *   center     innerCW     outerCW
     */
    val center = EDGE_SIZE * indexedCells.length

    // background size and color
    val imgSize = 2 * center + 1
    val canvas  = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB)
    val g       = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillOval(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    allCells.foreach { cell =>
      if (cell == startingCell) Grid.drawPoint(g, center, center)
      else { // We skip the central cell to avoid drawing its lateral boundary (since it only has outward neighbors)
        val innerRowRadius = EDGE_SIZE * cell.row
        val outerRowRadius = EDGE_SIZE * (cell.row + 1)
        val theta          = 2 * Math.PI / indexedCells(cell.row).length // cell angle size
        val thetaCCW       = theta * cell.column
        val thetaCW        = theta * (cell.column + 1)

        // Perhaps we could work directly on the polar coordinate system using the angles and radii, but we are going to
        // calculate the Cartesian coordinates of the different points using trigonometry.
        val innerCCWx = center + (innerRowRadius * Math.cos(thetaCCW))
        val innerCCWy = center + (innerRowRadius * Math.sin(thetaCCW))
        val innerCWx  = center + (innerRowRadius * Math.cos(thetaCW))
        val innerCWy  = center + (innerRowRadius * Math.sin(thetaCW))
        val outerCCWx = center + (outerRowRadius * Math.cos(thetaCCW))
        val outerCCWy = center + (outerRowRadius * Math.sin(thetaCCW))
        val outerCWx  = center + (outerRowRadius * Math.cos(thetaCW))
        val outerCWy  = center + (outerRowRadius * Math.sin(thetaCW))

        val inwardWall    = new Line2D.Double(innerCCWx, innerCCWy, innerCWx, innerCWy)
        val outwardWall   = new Line2D.Double(outerCCWx, outerCCWy, outerCWx, outerCWy)
        val clockwiseWall = new Line2D.Double(innerCWx, innerCWy, outerCWx, outerCWy)

        val inwardCelOpt = getInwardCellOf(cell)
        val outwardCells = getOutwardsCellsOf(cell)
        val clockwiseOpt = getClockwiseCellOf(cell)

        if (inwardCelOpt.isEmpty || !cell.isLinkedTo(inwardCelOpt.get)) g.draw(inwardWall)
        if (clockwiseOpt.isEmpty || !cell.isLinkedTo(clockwiseOpt.get)) g.draw(clockwiseWall)
        if (outwardCells.isEmpty) g.draw(outwardWall)

        if (cell.isEnd) {
          val thetaC  = midpoint(thetaCCW, thetaCW)
          val radiusC = midpoint(innerRowRadius, outerRowRadius)
          val cx      = center + (radiusC * Math.cos(thetaC))
          val cy      = center + (radiusC * Math.sin(thetaC))
          Grid.drawPoint(g, cx, cy)
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
