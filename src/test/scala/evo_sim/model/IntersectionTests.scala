import evo_sim.model._
import org.scalatest.FunSuite

class IntersectionTests extends FunSuite {
  private val circle1 = BoundingBox.Circle(point = Point2D(100, 100), radius = 10)
  private val circle2 = BoundingBox.Circle(point = Point2D(90, 90), radius = 10)
  private val rect = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40)
  private val tri = BoundingBox.Triangle(point = Point2D(110, 110), height = 40)

  test("Circle-Rectangle detection") {
    assert(Intersection.intersected(circle1, rect))
  }

  test("Circle-Circle detection") {
    assert(Intersection.intersected(circle1, circle2))
  }

  test("Circle-Triangle detection") {
    assert(Intersection.intersected(circle1, tri))
  }

  test("Triangle-Triangle detection") {
    assert(!Intersection.intersected(tri, tri))
  }

  test("Rectangle-Rectangle detection") {
    assert(!Intersection.intersected(rect, rect))
  }

  test("Rectangle-Triangle detection") {
    assert(!Intersection.intersected(rect, tri))
  }
}