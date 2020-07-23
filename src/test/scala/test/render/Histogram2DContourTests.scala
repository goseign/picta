package org.carbonateresearch.picta.render

import org.carbonateresearch.picta.UnitTestUtils.{validateJson, x_double, y_double}
import org.carbonateresearch.picta._
import org.carbonateresearch.picta.options.Marker
import org.scalatest.funsuite.AnyFunSuite

class Histogram2DContourTests extends AnyFunSuite {

  val plotFlag = false

  test("XY.Histogram2dContour") {
    val series = XY(x_double, y_double) asType HISTOGRAM2DCONTOUR
    val layout = ChartLayout("XY.Histogram2dContour")
    val chart = Chart() addSeries series setChartLayout layout setConfig(false, false)
    if (plotFlag) chart.plot
    assert(validateJson(chart.serialize.toString))
  }

  test("XY.Histogram2dContour.WithDensity") {
    val marker = Marker() setColor "rgb(102,0,0)"
    val series1 = XY(x_double, y_double) asType SCATTER drawStyle MARKERS setName "points" setMarker marker

    val series2 = (
      XY(x_double, y_double)
        setName "density"
        asType HISTOGRAM2DCONTOUR
        setHist2dOptions(ncontours = 20, reversescale = false, showscale = true)
      )

    val series3 = XY(x = x_double) asType HISTOGRAM setName "histogram" setAxes(XAxis(), YAxis(2))
    val series4 = (XY(y_double) setName "y density" asType HISTOGRAM setAxis XAxis(2) setMarker marker
      setHistOptions(orientation = HORIZONTAL))

    val ax1 = XAxis(showgrid = false) setDomain(0.0, 0.85)
    val ax2 = YAxis(showgrid = false) setDomain(0.0, 0.85)
    val ax3 = XAxis(position = 2, showgrid = false) setDomain(0.85, 1.0)
    val ax4 = YAxis(position = 2, showgrid = false) setDomain(0.85, 1.0)

    val layout = (ChartLayout("XY.Histogram2dContour.WithDensity", autosize = false) setAxes(ax1, ax2, ax3, ax4))

    val chart = Chart() addSeries(series1, series2, series3, series4) setChartLayout layout

    if (plotFlag) chart.plot
    assert(validateJson(chart.serialize.toString))
  }
}