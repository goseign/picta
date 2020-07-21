package org.carbonateresearch.picta

import org.carbonateresearch.picta
import org.carbonateresearch.picta.UnitTestUtils._
import org.scalatest.funsuite.AnyFunSuite

class ChartLayoutTests extends AnyFunSuite {

  val plotFlag = false

  /** Empty Chart Test -> Simply displays an empty set of axis, with a generic title */
  test("XY.Empty") {
    val chart = Chart()
    val canvas = Canvas() addCharts chart
    if (plotFlag) canvas.plot
    assert(validateJson(chart.serialize.toString))
  }

  /** Test multiple series plotting and axis composition */
  test("XY.Axis.Composition") {
    // 1. define the axis to plot on the chart
    val ax0 = XAxis(position = 1, title = " x axis 1")
    val ax1 = YAxis(position = 1, title = "y axis 1")
    val ax2 = YAxis(position = 2, title = "y axis 2", side = RIGHT, overlaying = YAxis())

    // 2. define the data to display on the chart
    val series1 = XY(x = x_int, y = y_double) asType SCATTER drawSymbol MARKERS setAxis YAxis(2)
    val series2 = XY(x = x_double, y = y_int) asType SCATTER drawSymbol MARKERS

    // 3. combine elements into a single chart
    val chart = (
      Chart()
        addSeries(series1, series2)
        setTitle "XY.Axis.Composition"
        addAxes(ax0, ax1, ax2)
        setConfig(false, false)
    )

    if (plotFlag) chart.plot
    assert(validateJson(chart.serialize.toString))
  }
}