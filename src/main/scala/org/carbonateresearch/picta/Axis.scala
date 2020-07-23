package org.carbonateresearch.picta

import org.carbonateresearch.picta.OptionWrapper._
import org.carbonateresearch.picta.common.Monoid._
import ujson.{Obj, Value}

sealed trait Side
case object RIGHT extends Side
case object LEFT extends Side

/** Specifies the behaviour of axes that are inside a plot. */
trait Axis extends Component {

  /** Used by the Picta library for book-keeping purposes. Specifies the direction of the axis. */
  private[picta] val orientation: String

  /** Specifies where on the chart this axis is displayed. */
  val position: Opt[Int]

  /** This sets the axis title. */
  val title: Opt[String]

  /** This determines which side the axis will be shown on. */
  val side: Opt[Side]

  /** This is used if we have more than one axis, and want to set which base axis it is mirroring. */
  val overlaying: Opt[Axis]

  /** Specifies the domain the Chart. */
  val domain: Opt[(Double, Double)]

  /** Specifies the range the Chart. */
  val range: Opt[(Double, Double)]

  /** Specifies the format of the axis ticks. */
  val tickformat: Opt[String]

  /** Specifies whether the grid is shown on the plot. */
  val showgrid: Boolean

  /** Specifies whether the zeroline for each axis are shown. */
  val zeroline: Boolean

  /** Specifies whether the axis is visibly drawn on the chart. */
  val showline: Boolean

  def setTitle(new_title: String): Axis

  def setDomain(new_domain: (Double, Double)): Axis

  def setRange(new_range: (Double, Double)): Axis

  private[picta] def getPosition(): String = position.option match {
    case Some(x) if x != 1 =>  x.toString
    case _ => ""
  }

  /** An internal function that converts a user entered key into one that the plotly library can understand */
  private def convertPosition(position: String): String = orientation + position

  private[picta] def serialize(): Value = {
    val meta = Obj(
      "showgrid" -> showgrid,
      "zeroline" -> zeroline,
      "showline" -> showline
    )

    val title_ = title.option match {
      case Some(x) => Obj("title" -> Obj("text" -> x))
      case None => jsonMonoid.empty
    }

    val side_ = side.option match {
      case Some(x) => Obj("side" -> x.toString.toLowerCase)
      case None => jsonMonoid.empty
    }

    val overlaying_ = overlaying.option match {
      case Some(x) if x.getPosition() != "" => Obj("overlaying" -> x.getPosition())
      case Some(x) if x.getPosition() == "" => Obj("overlaying" -> x.orientation.take(1))
      case None => jsonMonoid.empty
    }

    val domain_ = domain.option match {
      case Some(x) => Obj("domain" -> List(x._1, x._2))
      case None => jsonMonoid.empty
    }

    val range_ = range.option match {
      case Some(x) => Obj("range" -> List(x._1, x._2))
      case None => jsonMonoid.empty
    }

    val tickformat_ = tickformat.option match {
      case Some(x) => Obj("tickformat" ->x)
      case None => jsonMonoid.empty
    }

    val position_ = getPosition()

    Obj(convertPosition(position_) -> List(title_, meta, side_, overlaying_, domain_, range_, tickformat_)
      .foldLeft(jsonMonoid.empty)((a, x) => a |+| x))
  }
}

final case class XAxis (position: Opt[Int] = Blank, title: Opt[String] = Blank, side: Opt[Side] = Blank, overlaying: Opt[XAxis] = Blank,
                  domain: Opt[(Double, Double)] = Blank, range: Opt[(Double, Double)] = Blank, tickformat: Opt[String] = Blank, showgrid: Boolean = true,
                  zeroline: Boolean = false, showline: Boolean = false) extends Axis {

  override val orientation: String = "xaxis"

  def setTitle(new_title: String): XAxis = this.copy(title = new_title)
  def setDomain(new_domain: (Double, Double)): Axis = this.copy(domain = new_domain)
  def setRange(new_range: (Double, Double)): Axis = this.copy(range = new_range)
  def setTickFormat(new_format: String): Axis = this.copy(tickformat = new_format)
}

final case class YAxis (position: Opt[Int] = Blank, title: Opt[String] = Blank, side: Opt[Side] = Blank, overlaying: Opt[YAxis] = Blank,
                        domain: Opt[(Double, Double)] = Blank, range: Opt[(Double, Double)] = Blank, tickformat: Opt[String] = Blank, showgrid: Boolean = true,
                        zeroline: Boolean = false, showline: Boolean = false) extends Axis {

  override val orientation: String = "yaxis"

  def setTitle(new_title: String): YAxis = this.copy(title = new_title)
  def setDomain(new_domain: (Double, Double)): Axis = this.copy(domain = new_domain)
  def setRange(new_range: (Double, Double)): Axis = this.copy(range = new_range)
  def setTickFormat(new_format: String): Axis = this.copy(tickformat = new_format)
}
