package org.carbonateresearch.picta

import org.carbonateresearch.picta.options.{Line, Marker}
import org.scalatest.funsuite.AnyFunSuite
import upickle.default.write
import org.carbonateresearch.picta.UnitTestUtils._

class LineTests extends AnyFunSuite {
  test("Line.Constructor.Basic") {
    val line = Line() setColor "rgb(255, 255, 255, 1)"
    val test = """{"width":0.5,"color":"rgb(255, 255, 255, 1)"}"""
    assert(test == write(line.serialize))
  }

  test("Line.Constructor.Advanced") {
    val line = Line() setWidth 0.1 setColor List("rgb(255, 255, 255, 1)", "rgb(255, 255, 255, 1)")
    val test = """{"width":0.1,"color":["rgb(255, 255, 255, 1)","rgb(255, 255, 255, 1)"]}"""
    assert(test == write(line.serialize))
  }

  test("Line.withMarker") {
    val marker = Marker() setSymbol "circle" setColor "rgb(17, 157, 255)" setLine Line(width = 2)
    val data = XY(x_int, y_int, name="test", `type`=SCATTER, mode=MARKERS) setMarker marker
    val chart = Chart() addSeries data setLayout Layout() setConfig config
    assert(validateJson(chart.serialize.toString))
  }
}