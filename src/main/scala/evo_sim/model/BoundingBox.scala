package evo_sim.model

import BoundingBox._

case class Point2D(x: Int, y:Int)
//Bounding Box trait, Point(centered)
trait BoundingBox {
  def point: Point2D
}

object BoundingBox {
  //case classes for the different types of Bounding Boxes
  //Circle: Point & Radius
  case class Circle private[BoundingBox](point: Point2D, radius: Int) extends BoundingBox
  //Rectangle: Point & w + h
  case class Rectangle private[BoundingBox](point: Point2D, width: Int, height: Int) extends BoundingBox
  //Triangle: Point & h + angle (angle is defaulted as 60 -> equilateral), apothem = h/3*2 -> circe radius circumscribed in the triangle
  case class Triangle private[BoundingBox](point: Point2D, height: Int, angle: Double = 60.0) extends BoundingBox
  //apply methods
  def apply(point: Point2D, radius: Int): Circle = Circle(point, radius)
  def apply(point: Point2D, width: Int, height: Int): Rectangle = Rectangle(point, width, height)
  def apply(point: Point2D, height: Int, angle : Double): Triangle = Triangle(point, height, angle)

  def triangleVertices(tri: Triangle) : (Double, Double, Double, Double, Double, Double) = {
    /** return a tuple3 with the vertices
     *  v1 = center.x, center.y  + radius           -> upper vertices
     *  v2 = center.x - radius, center.y  - radius  -> bottom left vertices
     *  v1 = center.x + radius, center.y  - radius  -> bottom right vertices
     */
    val radius: Double = tri.height/3*2
    (tri.point.x, tri.point.y + radius,tri.point.x - radius, tri.point.y - radius, tri.point.x + radius, tri.point.y - radius)
  }
}

//creation bounding boxes
/*
val circle = Circle((1,2), 4)
println(circle.point + ", " + circle.radius + "->" + circle.getClass)
val rect = Rectangle((0,0), 5, 5)
println(rect.point + ", " + rect.width + ", " + rect.height + "->" + rect.getClass)
val tri = Triangle((7,7), 8)
println(tri.point + ", " + tri.height + ", " + tri.angle + "->" + tri.getClass)*/

object Intersection {
  //Intersection between a circle and any other entity (Circle, Rect, Triangle)
  def intersected(body1: Circle, body2: BoundingBox): Boolean = body2 match {
    //distance between centers, then check if is less than the sum of both the circle radius
    case circle: Circle => Math.hypot(body1.point.x - circle.point.x, body1.point.y - circle.point.y) < (body1.radius + circle.radius) //https://stackoverflow.com/questions/8367512/how-do-i-detect-intersections-between-a-circle-and-any-other-circle-in-the-same
    //collision between two rectangles (https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection#:~:text=One%20of%20the%20simpler%20forms,a%20collision%20does%20not%20exist.)
    /**
     * Bx + Bw > Ax &&
     * By + Bh > Ay &&
     * Ax + Aw > Bx &&
     * Ay + Ah > By;
     */
    case rectangle: Rectangle => rectangle.point.x + rectangle.width > body1.point.x &&
      rectangle.point.y + rectangle.height > body1.point.y &&
      body1.point.x + body1.radius > rectangle.point.x &&
      body1.point.y + body1.radius > rectangle.point.y
    //treat the triangle as a circle, simpler collision, apothem * 2 = circe radius circumscribed in the triangle
    case triangle: Triangle => Math.hypot(body1.point.x - triangle.point.x, body1.point.y - triangle.point.y) < (body1.radius + triangle.height / 3 * 2)
    case _ => false
  }
}
