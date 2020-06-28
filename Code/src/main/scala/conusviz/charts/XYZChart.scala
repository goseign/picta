package conusviz.charts

import conusviz.Html.{plotChart, plotChart_inline}
import conusviz.traces._
import conusviz.options.ConfigOptions.Config
import conusviz.options.LayoutOptions.Layout
import ujson.{Obj, Value}
import upickle.default._
import almond.interpreter.api.OutputHandler

sealed trait XYZ extends Chart {
  val traces: List[Value]
  val layout: Value
  val config: Value
}

/*
* Class for constructing a XYZ plot. This is typically three-dimensional data
* @param data: A list of traces that we wish to display on the surface plot
* @param l: This is a layout class instance specifying the options to do with layout
* @param c: This is a config class instance specifying the options to do with general configuration
* */
final case class XYZChart[T0, T1, T2](data: List[XYZTrace[T0, T1, T2]] = Nil, l: Layout = Layout(), c: Config = Config()) extends XYZ {

  val traces: List[Value] = data.map(t => t.serialize)
  val layout: Value = l.serialize
  val config: Value = c.serialize

  def serialize: Value = Obj("traces" -> traces, "layout" -> layout, "config" -> config)
  def plot(): Unit = plotChart(traces, layout, config)
  def plot_inline()(implicit publish: OutputHandler): Unit = plotChart_inline(traces, layout, config)
}

