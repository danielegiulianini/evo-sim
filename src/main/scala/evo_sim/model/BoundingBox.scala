/*package evo_sim.model



trait Shape
case class Rectangle(height: Int, width: Int) extends Shape
case class Circle(radius: Int) extends Shape
case class Triangle(side1: Int, side2: Int, side3: Int) extends Shape


trait BoundingBox {
  def position: (Int, Int)
  def shape : Shape
}

case class TriangularBoundingBox(override val shape: Triangle, override val position : (Int, Int)) extends BoundingBox
case class RectangularBoundingBox(override val shape: Rectangle, override val position : (Int, Int)) extends BoundingBox
case class CircularBoundingBox(override val shape: Rectangle, override val position : (Int, Int)) extends BoundingBox*/
