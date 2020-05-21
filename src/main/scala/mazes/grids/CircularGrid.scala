package mazes.grids

import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage

import mazes.Utils.{PositiveInt, random}
import mazes.grids.CircularGrid._

import scala.collection.mutable

final case class CircularGrid(rows: PositiveInt, columns: PositiveInt) extends Grid {

  override val rowsArray: Array[Array[Cell]] = Array.ofDim[Array[Cell]](rows.value)

  rowsArray(0) = Array[Cell](Cell(0, 0))

  var rowHeight: Double = 1.0 / rows.value

  for (row <- 1 until rows.value) {
    val radius              = row.toFloat / rows.value
    val circunference       = 2 * scala.math.Pi * radius
    val previousCount       = rowsArray(row - 1).length
    val estimated_cell_widh = circunference / previousCount
    val ratio               = scala.math.ceil(estimated_cell_widh / rowHeight).toInt
    val cells               = previousCount * ratio
    rowsArray(row) = Array.tabulate[Cell](cells)(i => Cell(row, i))
  }

  override def getRandomRow: Int = random.nextInt(rowsArray.length - 1)

  override def getRandomColumn: Int = random.nextInt(2)

  override def getCell(row: Int, column: Int): Option[Cell] = rowsArray.lift(row).flatMap(_.lift(column))

//  private def isStartingCell(cell: Cell): Boolean = cell.row == 0 && cell.column == 0

  val outwardCellsMap: mutable.Map[Cell, Set[Cell]] = mutable.Map.empty

  for (row <- rowsArray.indices) {
    for (column <- rowsArray(row).indices) {
      val cell      = rowsArray(row)(column)
      val parentOpt = getInwardCellOf(cell)
      if (parentOpt.isDefined) {
        val bob = outwardCellsMap.getOrElse(parentOpt.get, Set()) ++ Set(cell)
        outwardCellsMap.put(parentOpt.get, bob)
      }
    }
  }

  def getInwardCellOf(cell: Cell): Option[Cell] = {
    if (cell.row > 0) {
      val ratio  = rowsArray(cell.row).length / rowsArray(cell.row - 1).length
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
      cell.row == 0 && cell.column == 0)

  // Creo que el square wid tiene el calculo de las coordenadas poco intuitivo... el edge size debería de ir antes de rows y columns
  // i y j deberían de tener mejores nombres a lo mejor
  // proablemente meter en old el test que habías hecho para flaminem?

  def makePng(fileName: String): Unit = {

    // background size and color
    val imgSize = 2 * EDGE_SIZE * rowsArray.length + 1
    val canvas  = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
    val g       = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillOval(0, 0, canvas.getWidth, canvas.getHeight)

    val center = imgSize / 2

    // walls
    g.setColor(Color.BLACK)
    for (row <- rowsArray.indices) {
      for (column <- rowsArray(row).indices) {
        val theta       = 2 * scala.math.Pi / rowsArray(row).length
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

        val currentCellOpt = rowsArray.lift(row).flatMap(_.lift(column))
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
