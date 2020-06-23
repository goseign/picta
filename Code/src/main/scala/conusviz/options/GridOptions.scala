package conusviz.options

import ujson.{Obj, Value}
import upickle.default._

trait GridOptions {

}

object GridOptions {

  case class Grid(rows: Int = 1, columns: Int = 1, pattern: String = "independent") {
    def serialize(): Value = {
      val grid = Obj("rows" -> rows, "columns" -> columns, "pattern" -> pattern)
      transform(grid).to(Value)
    }

  }


}