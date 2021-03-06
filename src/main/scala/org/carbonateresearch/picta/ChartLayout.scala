/* Author:          Fazal Khan
   Github:          acse-fk4517
   Github Repo:     https://github.com/acse-2019/irp-acse-fk4517
*/
package org.carbonateresearch.picta

import org.carbonateresearch.picta.OptionWrapper.{Blank, Empty, Opt}
import org.carbonateresearch.picta.common.Monoid.jsonMonoid
import org.carbonateresearch.picta.options._
import ujson.{Obj, Value}

/** Determines how the hover mode interactions behave. */
trait HoverMode

/** Hover label appears for the closest data point in the x direction. */
case object Closest_X extends HoverMode {
  override def toString: String = "x"
}

/** Hover label appears for the closest data point in the y direction. */
case object CLosest_Y extends HoverMode {
  override def toString: String = "y"
}

/** Hover label appears for the closest data point. */
case object CLOSEST extends HoverMode

/** Hover label are disabled. */
case object FALSE extends HoverMode

/** Support to be added . */
case object X_UNIFIED extends HoverMode

case object Y_UNIFIED extends HoverMode

/**
 * Specifies the layout for the chart.
 *
 * @param title: Sets the chart title.
 * @param axes: Sets the chart title.
 * @param legend: This is the component that configures the legend for the chart.
 * @param auto_size
 * @param margin
 * @param map_options: This is used for Map charts only and configures the geo component for a Map chart.
 * @param multi_chart: Spcifies whether this chart will have multiple axes.
 * @param hover_distance: Specifies the hover distance.
 * @param show_legend: Toggles whether the legend should be displayed.
 * @param hover_mode: Determines how the hover mode interaction behaves.
 * @param height: This sets the height for the chart.
 * @param width: This sets the width for the chart.
 * @param XYZ: A flag value used by Picta to determine whether it should apply XY (2D) or XYZ (3D) options to the chart layout.
 */
final case class ChartLayout
(title: Opt[String] = Blank, axes: Opt[List[Axis]] = Empty, legend: Opt[Legend] = Blank, auto_size: Opt[Boolean] = Blank,
 margin: Opt[Margin] = Blank, map_options: Opt[MapOption] = Blank, multi_chart: Opt[MultiChart] = Blank, hover_distance: Opt[Int] = Blank,
 show_legend: Boolean = true, hover_mode: HoverMode = CLOSEST, height: Int = 550, width: Int = 600, private[picta] val XYZ: Boolean = false)
  extends Component {

  def setTitle(new_title: String) = this.copy(title = new_title)

  def setAxes(new_axes: List[Axis]) = axes.option match {
    /* add to the end of existing list. This ensures things that when we have two of the same keys,
    the ones at the right are preserved. This is because below we foldleft when merging the axes */
    case Some(lst) => this.copy(axes = lst ::: new_axes)
    case None => this.copy(axes = new_axes)
  }

  def setAxes(new_axis: Axis*) = axes.option match {
    /* add to the end of existing list. This ensures things that when we have two of the same keys,
    the ones at the right are preserved. This is because below we foldleft when merging the axes */
    case Some(lst) => this.copy(axes = lst ::: new_axis.toList)
    case None => this.copy(axes = new_axis.toList)
  }

  def setLegend(new_legend: Legend) = this.copy(legend = new_legend, show_legend = true)

  def setLegend(x: Opt[Double] = Blank, y: Opt[Double] = Blank, orientation: Orientation = VERTICAL,
                xanchor: Opt[Anchor] = Blank, yanchor: Opt[Anchor] = Blank) = {

    val new_legend = Legend(x = x, y = y, orientation = orientation, xanchor = xanchor, yanchor = yanchor)
    this.copy(legend = new_legend)
  }

  def setAutosize(new_autosize: Boolean) = this.copy(auto_size = new_autosize)

  def setMargin(new_margin: Margin) = this.copy(margin = new_margin)

  def setMapOption(new_geo: MapOption) = this.copy(map_options = new_geo)

  def setMultiChart(new_minigrid: MultiChart) = this.copy(multi_chart = new_minigrid)

  def showLegend(new_showlegend: Boolean) = this.copy(show_legend = new_showlegend)

  def setHoverMode(new_hovermode: HoverMode) = this.copy(hover_mode = new_hovermode)

  def setHeight(new_height: Int) = this.copy(height = new_height)

  def setWidth(new_width: Int) = this.copy(width = new_width)

  def setDimensions(new_height: Int, new_width: Int) = this.copy(height = new_height, width = new_width)

  private[picta] def setXYZ(XYZ: Boolean) = this.copy(XYZ = XYZ)

  private[picta] def serialize: Value = {
    val dim = Obj("height" -> height, "width" -> width, "hovermode" -> hover_mode.toString.toLowerCase)

    val title_ = title.option match {
      case Some(x) => Obj("title" -> Obj("text" -> x))
      case None => jsonMonoid.empty
    }

    val showlegend_ = Obj("showlegend" -> show_legend)

    val autosize_ = auto_size.option match {
      case Some(x) => Obj("autosize" -> x)
      case None => jsonMonoid.empty
    }

    val geo_ = map_options.option match {
      case Some(x) => Obj("geo" -> x.serialize)
      case None => jsonMonoid.empty
    }

    val margin_ = margin.option match {
      case Some(x) => Obj("margin" -> x.serialize)
      case None => jsonMonoid.empty
    }

    val minigrid_ = multi_chart.option match {
      case Some(x) => Obj("grid" -> x.serialize)
      case None => jsonMonoid.empty
    }

    val legend_ = legend.option match {
      case Some(x) => Obj("legend" -> x.serialize)
      case _ => jsonMonoid.empty
    }

    val hoverdistance_ = hover_distance.option match {
      case Some(x) => Obj("hoverdistance" -> x)
      case _ => jsonMonoid.empty
    }

    val combined = List(dim, title_, showlegend_, autosize_, geo_, margin_, minigrid_, legend_, hoverdistance_)
      .foldLeft(jsonMonoid.empty)((a, x) => a |+| x)

    axes.option match {
      case Some(lst) =>
        if (XYZ == true) {
          val combined_axes = lst.foldLeft(jsonMonoid.empty)((a, x) => a |+| x.serialize)
          val scene = Obj("scene" -> combined_axes)
          List(scene).foldLeft(combined)((a, x) => a |+| x)
        }
        /* if we have a XY chart, we can filter out the zaxis as they will never be used */
        else lst.filter(axis => axis.orientation != "zaxis").foldLeft(combined)((a, x) => a |+| x.serialize)
      case _ => combined
    }
  }
}