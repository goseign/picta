package picta.options

import picta.common.Component
import picta.common.Monoid.jsonMonoid
import picta.common.OptionWrapper._
import ujson.{Obj, Value}

case class Margin(l: Opt[Int] = Blank, r: Opt[Int] = Blank, t: Opt[Int] = Blank, b: Opt[Int] = Blank) extends Component {

  override def serialize: Value = {
    val l_ = l.asOption match {
      case Some(x) => Obj("l" -> x)
      case None => jsonMonoid.empty
    }

    val r_ = r.asOption match {
      case Some(x) => Obj("r" -> x)
      case None => jsonMonoid.empty
    }

    val t_ = t.asOption match {
      case Some(x) => Obj("t" -> x)
      case None => jsonMonoid.empty
    }

    val b_ = b.asOption match {
      case Some(x) => Obj("b" -> x)
      case None => jsonMonoid.empty
    }

    List(l_, r_, t_, b_).foldLeft(jsonMonoid.empty)((a, x) => a |+| x)
  }
}
