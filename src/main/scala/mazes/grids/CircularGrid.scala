package mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import mazes.Utils.PositiveInt
import mazes.grids.CircularGrid._

import scala.collection.mutable.ArrayBuffer

final case class CircularGrid(rows: PositiveInt) extends Grid { // AKA polar grid

  /* In a polar grid, each row is delimited by a pair of concentric circles. Therefore, if we were to use a matrix to
   * represent the different cells of the maze, the outer ones would have to be much wider than the inner ones.
   * But we want each cell to be about the same height and width.
   * Hence, we need to build a jagged array and put more cells in the outer rows than in the inner rows.
   */
  override val gridCells: Vector[ArrayBuffer[Cell]] = {
    val rowHeight   = 1.0 // true value doesn't matter. In this context we only care about the proportions
    val indexedRows = Vector.fill(rows.value)(ArrayBuffer.empty[Cell])
    indexedRows(0) += Cell(0, 0)

    for (row <- 1 until rows.value) {
      // If the width of the cells in the previous row were too large for the current row (ratio > 1), we will decrease
      // it by inserting more cells or columns than before (usually double, except for the row at index 1).
      val innerRowRadius     = rowHeight * row
      val innerCircumference = 2 * scala.math.Pi * innerRowRadius
      val previousCellCount  = indexedRows(row - 1).length
      val previousCellWidth  = innerCircumference / previousCellCount
      val ratio              = scala.math.ceil(previousCellWidth / rowHeight).toInt
      val columns            = previousCellCount * ratio
      indexedRows(row) ++= ArrayBuffer.tabulate[Cell](columns)(column => Cell(row, column))
    }

    indexedRows
  }

  def getInwardCellOf(cell: Cell): Option[Cell] = {
    gridCells.lift(cell.row - 1).map(previousRow => gridCells(cell.row).length / previousRow.length) match {
      case Some(ratio) => getCell(cell.row - 1, cell.column / ratio)
      case _           => None
    }
  }

  def getOutwardsCellsOf(cell: Cell): Seq[Cell] = {
    if (cell.row == 0 & cell.column == 0) gridCells(1).toSeq
    else
      gridCells.lift(cell.row + 1).map(_.length / gridCells(cell.row).length) match {
        case None                      => Seq()
        case Some(ratio) if ratio == 2 => Seq(getCell(cell.row + 1, cell.column * ratio), getCell(cell.row + 1, cell.column * ratio + 1)).flatten
        case _                         => Seq(getCell(cell.row + 1, cell.column)).flatten // ratio == 1
      }
  }

  def getClockwiseCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column + 1)

  def getCounterClockwiseCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column - 1)

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
    val c = EDGE_SIZE * gridCells.length

    // background size and color
    val imgSize = 2 * c + 1
    val canvas  = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
    val g       = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillOval(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    for (row <- gridCells.indices) {
      for (column <- gridCells(row).indices) {
        val innerRowRadius = EDGE_SIZE * row
        val outerRowRadius = EDGE_SIZE * (row + 1)
        val theta          = 2 * scala.math.Pi / gridCells(row).length // cell angle size
        val thetaCCW       = theta * column
        val thetaCW        = theta * (column + 1)

        // Perhaps we could work directly on the polar coordinate system using the angles and radii, but we are going to
        // calculate the Cartesian coordinates of the different points using trigonometry
        val innerCCWx = c + (innerRowRadius * scala.math.cos(thetaCCW))
        val innerCCWy = c + (innerRowRadius * scala.math.sin(thetaCCW))
        val innerCWx  = c + (innerRowRadius * scala.math.cos(thetaCW))
        val innerCWy  = c + (innerRowRadius * scala.math.sin(thetaCW))
        val outerCWx  = c + (outerRowRadius * scala.math.cos(thetaCW))
        val outerCWy  = c + (outerRowRadius * scala.math.sin(thetaCW))

        val inwardWall    = new Line2D.Double(innerCCWx, innerCCWy, innerCWx, innerCWy)
        val clockwiseWall = new Line2D.Double(innerCWx, innerCWy, outerCWx, outerCWy)

        val currentCell  = gridCells(row)(column)
        val inwardCelOpt = getInwardCellOf(currentCell)
        val clockwiseOpt = getClockwiseCellOf(currentCell)

        if (inwardCelOpt.isEmpty || !currentCell.isLinkedTo(inwardCelOpt.get)) g.draw(inwardWall)
        if (clockwiseOpt.isEmpty || !currentCell.isLinkedTo(clockwiseOpt.get)) g.draw(clockwiseWall)

      }
    }

    g.dispose()
    Grid.writeImage(canvas, fileName)
  }

}

object CircularGrid {

  val EDGE_SIZE = 40

}
