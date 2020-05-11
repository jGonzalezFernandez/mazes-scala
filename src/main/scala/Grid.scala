import scala.collection.mutable
import Utils._

trait Grid {

  def rows: PositiveInt

  def columns: PositiveInt

  case class Cell(row: Int, column: Int) {
    var visited: Boolean = false
    private val links    = mutable.Set.empty[Cell]

    def linkTo(cell: Cell): Unit = {
      links += cell
      cell.links += this
    }

    def unlinkFrom(cell: Cell): Unit = {
      links -= cell
      cell.links -= this
    }

    def isLinkedTo(cell: Cell): Boolean = links.contains(cell) // directly linked

  }

  val cellMatrix: Vector[Vector[Cell]] = Vector.tabulate[Cell](rows.value, columns.value)((i, j) => Cell(i, j))

  def getCell(row: Int, column: Int): Option[Cell] = cellMatrix.lift(row).flatMap(_.lift(column))

  def getRandomCell(cells: collection.Seq[Cell]): Option[Cell] = if (cells.isEmpty) None else Some(cells(random.nextInt(cells.size)))

  def getRandomRow: Int = random.nextInt(rows.value)

  def getRandomColumn: Int = random.nextInt(columns.value)

  def getNeighboursOf(cell: Cell): Seq[Cell]

  def makePng(fileName: String): Unit

}
