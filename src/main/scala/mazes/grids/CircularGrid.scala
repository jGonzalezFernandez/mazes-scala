package mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import mazes.Utils.PositiveInt
import mazes.grids.CircularGrid._

import scala.collection.mutable
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
      val innerRowRadius              = rowHeight * row
      val innerCircumferencePerimeter = 2 * scala.math.Pi * innerRowRadius
      val previousCellCount           = indexedRows(row - 1).length
      val previousCellWidth           = innerCircumferencePerimeter / previousCellCount
      val ratio                       = scala.math.ceil(previousCellWidth / rowHeight).toInt
      val columns                     = previousCellCount * ratio
      indexedRows(row) ++= ArrayBuffer.tabulate[Cell](columns)(column => Cell(row, column))
    }

    indexedRows
  }

  val outwardCellsMap: mutable.Map[Cell, Set[Cell]] = mutable.Map.empty

  for (row <- gridCells.indices) {
    for (column <- gridCells(row).indices) {
      val cell      = gridCells(row)(column)
      val parentOpt = getInwardCellOf(cell)
      if (parentOpt.isDefined) {
        val bob = outwardCellsMap.getOrElse(parentOpt.get, Set()) ++ Set(cell)
        outwardCellsMap.put(parentOpt.get, bob)
      }
    }
  }

  def getInwardCellOf(cell: Cell): Option[Cell] = {
    if (cell.row > 0) {
      val ratio  = gridCells(cell.row).length / gridCells(cell.row - 1).length
      val parent = getCell(cell.row - 1, cell.column / ratio)
      parent
    }
    else None
  }

  // two!
  def getOutwardCellsOf(cell: Cell): Seq[Cell] = outwardCellsMap.getOrElse(cell, Seq()).toSeq

  def getClockwiseCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column + 1)

  def getCounterClockwiseCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] =
    (Seq(getInwardCellOf(cell), getClockwiseCellOf(cell), getCounterClockwiseCellOf(cell)).flatten ++ getOutwardCellsOf(cell)).filterNot(cell =>
      cell.row == 0 && cell.column == 0
    )

  // Creo que el square wid tiene el calculo de las coordenadas poco intuitivo... el edge size debería de ir antes de rows y columns
  // i y j deberían de tener mejores nombres a lo mejor
  // proablemente meter en old el test que habías hecho para flaminem?

  def makePng(fileName: String): Unit = {

    // background size and color
    val imgSize = 2 * EDGE_SIZE * gridCells.length + 1
    val canvas  = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
    val g       = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillOval(0, 0, canvas.getWidth, canvas.getHeight)

    val center = imgSize / 2

    // walls
    g.setColor(Color.BLACK)
    for (row <- gridCells.indices) {
      for (column <- gridCells(row).indices) {
        val theta       = 2 * scala.math.Pi / gridCells(row).length
        val innerRadius = row * EDGE_SIZE
        val outerRadius = (row + 1) * EDGE_SIZE
        val thetaCCW    = column * theta
        val thetaCW     = (column + 1) * theta

        val ax = center + (innerRadius * scala.math.cos(thetaCCW))
        val ay = center + (innerRadius * scala.math.sin(thetaCCW))
        val cx = center + (innerRadius * scala.math.cos(thetaCW))
        val cy = center + (innerRadius * scala.math.sin(thetaCW))
        val dx = center + (outerRadius * scala.math.cos(thetaCW))
        val dy = center + (outerRadius * scala.math.sin(thetaCW))

        val southWall = new Line2D.Double(ax, ay, cx, cy)
        val eastWall  = new Line2D.Double(cx, cy, dx, dy)

        val currentCellOpt = gridCells.lift(row).flatMap(_.lift(column))
        //        val outwardCellsOpt     = currentCellOpt.flatMap(getOutwardCellsOf)
        val inwardCelOpt        = currentCellOpt.flatMap(getInwardCellOf)
        val clockwiseOpt        = currentCellOpt.flatMap(getClockwiseCellOf)
        val counterClockwiseOpt = currentCellOpt.flatMap(getCounterClockwiseCellOf)

        if (currentCellOpt.nonEmpty) {

          if (inwardCelOpt.isEmpty || !currentCellOpt.get.isLinkedTo(inwardCelOpt.get)) g.draw(southWall)
          //        if (outwardCellOpt.isEmpty || !currentCellOpt.get.isLinkedTo(outwardCellsOpt.get)) g.draw(southWall)
          if (clockwiseOpt.isEmpty || !currentCellOpt.get.isLinkedTo(clockwiseOpt.get)) g.draw(eastWall)
          //        if (counterClockwiseOpt.isEmpty || !currentCellOpt.get.isLinkedTo(counterClockwiseOpt.get)) g.draw(eastWall)
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
