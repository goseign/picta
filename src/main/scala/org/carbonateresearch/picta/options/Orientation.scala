package org.carbonateresearch.picta.options

sealed trait Orientation

case object VERTICAL extends Orientation {
  override def toString: String = "x"
}

case object HORIZONTAL extends Orientation {
  override def toString: String = "y"
}