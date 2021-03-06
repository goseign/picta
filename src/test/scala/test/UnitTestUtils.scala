/* Author:          Fazal Khan
   Github:          acse-fk4517
   Github Repo:     https://github.com/acse-2019/irp-acse-fk4517
*/
package org.carbonateresearch.picta

import org.carbonateresearch.picta.OptionWrapper._
import ColorOptions.Color

/**
 * This object contains helper functions that help in testing various functionality of the library.
 */
object UnitTestUtils {

  // create a common configuration to be used in all the tests
  val config: Config = Config(responsive = false)
  val x_random = List.range(1, 100).map(x => genRangeRandomDouble())
  val x_int = List.range(0, 100).map(x => genRangeRandomInt())
  val y_int = List.range(0, 100).map(x => genRangeRandomInt())
  val z_int = List.range(0, 100).map(x => genRangeRandomInt())
  val x_double = List.range(0, 100).map(x => genRangeRandomDouble())
  val y_double = List.range(0, 100).map(x => genRangeRandomDouble())
  val z_double = List.range(0, 100).map(x => genRangeRandomDouble())
  val x_str = List("1", "2", "3")
  val y_str = List("a", "b", "c")
  val z_str = List("my", "name", "is")
  val z_surface = List(
    List(8.83, 8.89, 8.81, 8.87, 8.9, 8.87),
    List(8.89, 8.94, 8.85, 8.94, 8.96, 8.92),
    List(8.84, 8.9, 8.82, 8.92, 8.93, 8.91),
    List(8.79, 8.85, 8.79, 8.9, 8.94, 8.92),
    List(8.79, 8.88, 8.81, 8.9, 8.95, 8.92),
    List(8.8, 8.82, 8.78, 8.91, 8.94, 8.92),
    List(8.75, 8.78, 8.77, 8.91, 8.95, 8.92),
    List(8.8, 8.8, 8.77, 8.91, 8.95, 8.94),
    List(8.74, 8.81, 8.76, 8.93, 8.98, 8.99),
    List(8.89, 8.99, 8.92, 9.1, 9.13, 9.11),
    List(8.97, 8.97, 8.91, 9.09, 9.11, 9.11),
    List(9.04, 9.08, 9.05, 9.25, 9.28, 9.27),
    List(9, 9.01, 9, 9.2, 9.23, 9.2),
    List(8.99, 8.99, 8.98, 9.18, 9.2, 9.19),
    List(8.93, 8.97, 8.97, 9.18, 9.2, 9.18)
  )

  def genRangeRandomInt(min: Int = 0, max: Int = 10000) = min + (max - min) * scala.util.Random.nextInt()

  def genRangeRandomDouble(min: Double = 0.0, max: Double = 10000.0) = min + (max - min) * scala.util.Random.nextDouble()

  // creates random XY for testing purposes
  def createXYSeries[T: Color]
  (numberToCreate: Int, count: Int = 0, length: Int = 10): List[Series] = {

    if (count == numberToCreate) Nil
    else {
      val xs = List.range(0, length).map(x => genRangeRandomDouble())
      val ys = xs.map(x => genRangeRandomDouble())
      val trace = XY(x = xs, y = ys, name = "trace" + count)
      trace :: createXYSeries(numberToCreate, count + 1, length)
    }
  }

  def createXYZSeries(numberToCreate: Int, count: Int = 0, length: Int = 10): List[Series] = {
    if (count == numberToCreate) Nil
    else {
      val xs = List.range(0, length).map(x => genRangeRandomDouble())
      val ys = xs.map(x => genRangeRandomDouble())
      val zs = xs.map(x => genRangeRandomDouble())
      val trace = XYZ(x = xs, y = ys, z = zs, name = "trace" + count, `type` = SCATTER3D)
      trace :: createXYZSeries(numberToCreate, count + 1, length)
    }
  }

  def validateJson(json: String, ignore: Opt[String] = Blank): Boolean = {
    val wd = os.pwd / "src" / "test" / "resources" / "javascript"

    // spawn a subprocess
    val sub = os.proc("node", "test.js").spawn(cwd = wd)

    // send the json to node
    sub.stdin.write(json)
    sub.stdin.flush()

    // get the response from node back
    val result = ujson.read(sub.stdout.readLine())

    // end the process
    sub.destroy()

    val errors = ignore.option match {
      case Some(x) => result.arr.filter(e => e("code").toString() != s""""${x}"""")
      case None => result.arr
    }

    if (errors.length > 0) errors.foreach(println)

    errors.length == 0
  }
}
