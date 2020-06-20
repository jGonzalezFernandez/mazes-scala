package jgonzalezfernandez.mazes.grids

import java.awt.Graphics2D
import java.awt.image.BufferedImage

import jgonzalezfernandez.mazes.Utils._

trait Grid {

  def rows: PositiveInt

  def indexedCells: Vector[collection.IndexedSeq[Cell]]

  lazy val allCells: Seq[Cell] = indexedCells.flatten

  def startingCell: Cell

  def getCell(row: Int, column: Int): Option[Cell] = indexedCells.lift(row).flatMap(_.lift(column))

  def getRandomCell: Cell = allCells(random.nextInt(allCells.size))

  def getRandomCell(cells: collection.Seq[Cell]): Option[Cell] = if (cells.isEmpty) None else Some(cells(random.nextInt(cells.size)))

  def getNeighboursOf(cell: Cell): Seq[Cell]

  def makePng(fileName: String): Unit

}

object Grid {

  // makePng Helpers

  def drawPoint(g: Graphics2D, cx: Double, cy: Double): Unit = {
    val size     = 6
    val halfSize = size / 2.0
    g.fillOval(Math.ceil(cx - halfSize).toInt, Math.ceil(cy - halfSize).toInt, size, size)
  }

  def writeImage(canvas: BufferedImage, fileName: String): Unit = {
    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(s"PNGs/$fileName.png"))
  }

}
