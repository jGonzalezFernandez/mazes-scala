import OrthogonalGrid._
import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import Utils.PositiveInt

case class OrthogonalGrid(rows: PositiveInt, columns: PositiveInt) extends Grid {

  def getNorthCellOf(cell: Cell): Option[Cell] = getCell(cell.row + 1, cell.column)

  def getSouthCellOf(cell: Cell): Option[Cell] = getCell(cell.row - 1, cell.column)

  def getEastCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column + 1)

  def getWestCellOf(cell: Cell): Option[Cell] = getCell(cell.row, cell.column - 1)

  def getNeighboursOf(cell: Cell): Seq[Cell] = Seq(getNorthCellOf(cell), getSouthCellOf(cell), getEastCellOf(cell), getWestCellOf(cell)).flatten

  def makePng(fileName: String): Unit = {
    // background size and color
    val width  = CELL_SIZE * columns.value + 1
    val height = CELL_SIZE * rows.value + 1
    val canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g      = canvas.createGraphics()
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    // walls
    g.setColor(Color.BLACK)
    for (row <- 0 until rows.value; column <- 0 until columns.value) {
      val x1 = column * CELL_SIZE
      val y1 = (rows.value - row - 1) * CELL_SIZE // Reminder: the y-axis points down in the default Java coordinate system (user space)
      val x2 = (column + 1) * CELL_SIZE
      val y2 = (rows.value - row) * CELL_SIZE

      val topWall    = new Line2D.Double(x1, y1, x2, y1)
      val bottomWall = new Line2D.Double(x1, y2, x2, y2)
      val rightWall  = new Line2D.Double(x2, y1, x2, y2)
      val leftWall   = new Line2D.Double(x1, y1, x1, y2)

      val currentCell  = cellMatrix(row)(column)
      val northCellOpt = getNorthCellOf(currentCell)
      val southCellOpt = getSouthCellOf(currentCell)
      val eastCellOpt  = getEastCellOf(currentCell)
      val westCellOpt  = getWestCellOf(currentCell)

      if (northCellOpt.isEmpty || !currentCell.isLinkedTo(northCellOpt.get)) g.draw(topWall)
      if (southCellOpt.isEmpty || !currentCell.isLinkedTo(southCellOpt.get)) g.draw(bottomWall)
      if (eastCellOpt.isEmpty || !currentCell.isLinkedTo(eastCellOpt.get)) g.draw(rightWall)
      if (westCellOpt.isEmpty || !currentCell.isLinkedTo(westCellOpt.get)) g.draw(leftWall)
    }

    g.dispose()
    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(s"$fileName.png"))
  }

}

object OrthogonalGrid {

  val CELL_SIZE = 30

}
