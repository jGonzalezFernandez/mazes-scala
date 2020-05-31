package jgonzalezfernandez.mazes.grids

import java.awt.image.BufferedImage

import jgonzalezfernandez.mazes.Utils._

trait Grid {

  def rows: PositiveInt

  def gridCells: Vector[collection.IndexedSeq[Cell]]

  def getCell(row: Int, column: Int): Option[Cell] = gridCells.lift(row).flatMap(_.lift(column))

  def getRandomCell: Cell = {
    val randomRow    = random.nextInt(gridCells.length)
    val randomColumn = random.nextInt(gridCells(randomRow).length)
    gridCells(randomRow)(randomColumn)
  }

  def getRandomCell(cells: collection.Seq[Cell]): Option[Cell] = if (cells.isEmpty) None else Some(cells(random.nextInt(cells.size)))

  def getNeighboursOf(cell: Cell): Seq[Cell]

  def makePng(fileName: String): Unit

}

object Grid {

  def writeImage(canvas: BufferedImage, fileName: String): Unit = {
    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(s"PNGs/$fileName.png"))
  }

}
