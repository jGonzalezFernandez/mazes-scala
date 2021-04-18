package jgonzalezfernandez.mazes.grids

import jgonzalezfernandez.mazes.Utils._

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File

trait Grid {

  def rows: PositiveInt

  def indexedCells: Vector[collection.IndexedSeq[Cell]]

  lazy val allCells: Seq[Cell] = indexedCells.flatten

  def startingCell: Cell

  def getCell(row: Int, column: Int): Option[Cell] = indexedCells.lift(row).flatMap(_.lift(column))

  def getNorthCellOf(cell: Cell): Option[Cell] = getCell(cell.row - 1, cell.column)

  def getSouthCellOf(cell: Cell): Option[Cell] = getCell(cell.row + 1, cell.column)

  def getEastCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column + 1)

  def getWestCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] = Seq(getNorthCellOf(cell), getSouthCellOf(cell), getEastCellOf(cell), getWestCellOf(cell)).flatten

  def getRandomCell: Cell = allCells(randomInt(allCells.length))

  def getRandomCell(cells: collection.Seq[Cell]): Option[Cell] = if (cells.isEmpty) None else Some(cells(randomInt(cells.length)))

  def makePng(fileName: String): File

}

object Grid {

  // makePng Helpers:

  def drawPoint(g: Graphics2D, cx: Double, cy: Double): Unit = {
    val size     = 6
    val halfSize = size / 2.0
    g.fillOval(Math.ceil(cx - halfSize).toInt, Math.ceil(cy - halfSize).toInt, size, size)
  }

  def writeImage(canvas: BufferedImage, fileName: String): File = {
    val file = new java.io.File(s"PNGs/$fileName.png")
    javax.imageio.ImageIO.write(canvas, "png", file)
    file
  }

}
