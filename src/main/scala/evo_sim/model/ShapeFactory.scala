package evo_sim.model

import BoundingBoxShape._

//Bounding Box trait, Point(centered)
trait BoundingBoxShape {
  def point: (Int, Int)
}

object BoundingBoxShape {
  //case classes for the different types of Bounding Boxes
  //Circle: Point & Radius
  case class Circle private[BoundingBoxShape](point: (Int, Int), radius: Int) extends BoundingBoxShape
  //Rectangle: Point & w + h
  case class Rectangle private[BoundingBoxShape](point: (Int, Int), width: Int, height: Int) extends BoundingBoxShape
  //Triangle: Point & h + angle (angle is defaulted as 60 -> equilateral), apothem = h/3*2 -> circe radius circumscribed in the triangle
  case class Triangle private[BoundingBoxShape](point: (Int, Int), height: Int, angle: Double = 60.0) extends BoundingBoxShape
  //apply methods
  def apply(point: (Int, Int), radius: Int): Circle = Circle(point, radius)
  def apply(point: (Int, Int), width: Int, height: Int): Rectangle = Rectangle(point, width, height)
  def apply(point: (Int, Int), height: Int, angle : Double): Triangle = Triangle(point, height, angle)
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
  def intersected(body1: Circle, body2: BoundingBoxShape): Boolean = body2 match {
    //distance between centers, then check if is less than the sum of both the circle radius
    case circle: Circle => Math.hypot(body1.point._1 - circle.point._1, body1.point._2 - circle.point._2) < (body1.radius + circle.radius) //https://stackoverflow.com/questions/8367512/how-do-i-detect-intersections-between-a-circle-and-any-other-circle-in-the-same
    //collision between two rectangles (https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection#:~:text=One%20of%20the%20simpler%20forms,a%20collision%20does%20not%20exist.)
    /**
     * Bx + Bw > Ax &&
     * By + Bh > Ay &&
     * Ax + Aw > Bx &&
     * Ay + Ah > By;
     */
    case rectangle: Rectangle => rectangle.point._1 + rectangle.width > body1.point._1 &&
      rectangle.point._2 + rectangle.height > body1.point._2 &&
      body1.point._1 + body1.radius > rectangle.point._1 &&
      body1.point._2 + body1.radius > rectangle.point._2
    //treat the triangle as a circle, simpler collision, apothem * 2 = circe radius circumscribed in the triangle
    case triangle: Triangle => Math.hypot(body1.point._1 - triangle.point._1, body1.point._2 - triangle.point._2) < (body1.radius + triangle.height / 3 * 2)
    case _ => false
  }
}
