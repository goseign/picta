package picta.options.histogram

import picta.common.Component
import picta.common.Monoid._
import picta.common.OptionWrapper._
import ujson.{Obj, Value}

class HistBins(start: Opt[Double] = Blank, end: Opt[Double] = Blank, size: Opt[Double] = Blank) extends Component {

  def serialize: Value = {
    val start_ = start.asOption match {
      case Some(x) => Obj("start" -> x)
      case None => jsonMonoid.empty
    }

    val end_ = end.asOption match {
      case Some(x) => Obj("end" -> x)
      case None => jsonMonoid.empty
    }

    val size_ = size.asOption match {
      case Some(x) => Obj("size" -> x)
      case None => jsonMonoid.empty
    }

    List(start_, end_, size_).foldLeft(jsonMonoid.empty)((a, x) => a |+| x)
  }
}

case class Xbins(start: Opt[Double] = Blank, end: Opt[Double] = Blank, size: Opt[Double] = Blank) extends
  HistBins(start = start, end = end, size = size)

case class Ybins(start: Opt[Double] = Blank, end: Opt[Double] = Blank, size: Opt[Double] = Blank) extends
  HistBins(start = start, end = end, size = size)