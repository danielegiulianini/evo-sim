package evo_sim.model.entities.entityStructure

import evo_sim.model.entities.entityStructure.BoundingBox._
import evo_sim.model.world.Constants

/**
 * The Point2D class defines a point representing a location in (x,y) coordinate space.
 *
 * @param x first coordinate
 * @param y second coordinate
 */
case class Point2D(x: Int, y: Int)

/**
 * Companion object of Point2D
 */
object Point2D{
  /**
   * @return a new Point2D in a random position within the World boundary.
   */
  def randomPosition(): Point2D = Point2D(new scala.util.Random().nextInt(Constants.WORLD_WIDTH.+(1)),
    new scala.util.Random().nextInt(Constants.WORLD_HEIGHT.+(1)))
}

/**
 * Bounding Box trait, [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Bounding Box.
 */
 sealed trait BoundingBox {
  /** The center of the Bounding box */
  def point: Point2D
}

/**
 * Bounding Box object, creates different Bounding Box types.
 */
object BoundingBox {

  //case classes for the different types of Bounding Boxes
  //Circle: Point & Radius
  /**
   * Circle Shape, used by [[evo_sim.model.entities.entityStructure.EntityStructure.Blob]].
   *
   * @param point  a [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Circle.
   * @param radius represent the radius of the Circle.
   */
  case class Circle(point: Point2D, radius: Int) extends BoundingBox

  //Rectangle: Point & w + h
  /**
   * Rectangle Shape, used by [[evo_sim.model.entities.entityStructure.EntityStructure.Obstacle]].
   *
   * @param point  a [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Rectangle.
   * @param width  represent the width of the Rectangle.
   * @param height represent the height of the Rectangle.
   */
  case class Rectangle(point: Point2D, width: Int, height: Int) extends BoundingBox

  //Triangle: Point & h + angle (angle is defaulted as 60 -> equilateral), apothem = h/3*2 -> circe radius circumscribed in the triangle
  /**
   * Triangle Shape, used by [[evo_sim.model.entities.entityStructure.EntityStructure.Food]].
   *
   * @param point  a [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Triangle.
   * @param height represent the height of the Triangle.
   * @param angle  represent the angle of the Triangle.
   */
  case class Triangle(point: Point2D, height: Int, angle: Double = Constants.DEF_EQUILATERAL_ANGLE) extends BoundingBox

  //apply methods
  /**
   * Apply Circle constructor.
   *
   * @param point  a [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Circle.
   * @param radius represent the radius of the Circle.
   * @return the Circle object.
   */
  def apply(point: Point2D, radius: Int): Circle = Circle(point, radius)

  /**
   * Apply Rectangle constructor.
   *
   * @param point  a [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Rectangle.
   * @param width  represent the width of the Rectangle.
   * @param height represent the height of the Rectangle.
   * @return the Rectangle object.
   */
  def apply(point: Point2D, width: Int, height: Int): Rectangle = Rectangle(point, width, height)

  /**
   * Apply Triangle constructor.
   *
   * @param point  a [[evo_sim.model.entities.entityStructure.Point2D]] represent the center of the Triangle.
   * @param height represent the height of the Triangle.
   * @param angle  represent the angle of the Triangle.
   * @return the Triangle object.
   */
  def apply(point: Point2D, height: Int, angle: Double): Triangle = Triangle(point, height, angle)
}

object Intersection {

  /**
   * Given two bounding box, verify if the two boxes are colliding.
   *
   * @param body1 the first body.
   * @param body2 the second body.
   * @return true if the bodies intersect, false otherwise.
   */
  //Intersection between entities (Circle, Rect, Triangle)
  def intersected(body1: BoundingBox, body2: BoundingBox): Boolean = (body1, body2) match {
    case (body1: Circle, circle: Circle) => circleIntersectsCircle(body1, circle)
    case (body1: Circle, rectangle: Rectangle) => circleIntersectsRectangle(body1, rectangle)
    case (body1: Circle, triangle: Triangle) => circleIntersectsTriangle(body1, triangle)
    case (body1: Rectangle, circle: Circle) => circleIntersectsRectangle(circle, body1)
    case (body1: Rectangle, rectangle: Rectangle) => rectangleIntersectsRectangle(body1, rectangle)
    case (body1: Rectangle, triangle: Triangle) => rectangleIntersectsTriangle(body1, triangle)
    case (body1: Triangle, circle: Circle) => circleIntersectsTriangle(circle, body1)
    case (body1: Triangle, rectangle: Rectangle) => rectangleIntersectsTriangle(rectangle, body1)
    case (body1: Triangle, triangle: Triangle) => triangleIntersectsTriangle(body1, triangle)
    case _ => false
  }

  // distance between centers, then check if is less than the sum of both the circle radius
  /**
   * Intersection between two circles.
   *
   * @param circle1 first circle.
   * @param circle2 second circle.
   * @return true if the bodies intersect, false otherwise.
   */
  private def circleIntersectsCircle(circle1: Circle, circle2: Circle) =
    Math.hypot(circle1.point.x - circle2.point.x, circle1.point.y - circle2.point.y) < (circle1.radius + circle2.radius)

  // Treat the triangle as a circle, simpler collision, apothem * 2 = circe radius circumscribed in the triangle
  /**
   * Intersection between two circles.
   *
   * @param circle   circle shape.
   * @param triangle triangle shape.
   * @return true if the bodies intersect, false otherwise.
   */
  private def circleIntersectsTriangle(circle: Circle, triangle: Triangle) =
    Math.hypot(circle.point.x - triangle.point.x, circle.point.y - triangle.point.y) < (circle.radius + triangle.height / 3 * 2)

  /**
   * Intersection between two circles.
   *
   * @param circle    circle shape.
   * @param rectangle rectangle shape.
   * @return true if the bodies intersect, false otherwise.
   */
  private def circleIntersectsRectangle(circle: Circle, rectangle: Rectangle) = {

    /*rectangle.point.x + rectangle.width > circle.point.x &&
      rectangle.point.y + rectangle.height > circle.point.y &&
      circle.point.x + circle.radius > rectangle.point.x &&
      circle.point.y + circle.radius > rectangle.point.y*/

    circleIntersectsCircle(circle, Circle(rectangle.point, (rectangle.width+rectangle.height)/2))
  }



  // Collision between two rectangles (https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection#:~:text=One%20of%20the%20simpler%20forms,a%20collision%20does%20not%20exist.)

  private def rectangleIntersectsRectangle(rectangle1: Rectangle, rectangle2: Rectangle) = false

  private def rectangleIntersectsTriangle(rectangle: Rectangle, triangle: Triangle) = false

  private def triangleIntersectsTriangle(triangle1: Triangle, triangle2: Triangle) = false


}
