import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._

object Utils {

  type PositiveInt = Int Refined Positive

  val random = new scala.util.Random

}
