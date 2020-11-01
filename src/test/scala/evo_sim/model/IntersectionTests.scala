import evo_sim.model._
import org.scalatest.{FunSpec, FunSuite}

class IntersectionTests extends FunSpec {
  private val circle1 = BoundingBox.Circle(point = Point2D(100, 100), radius = 10)
  private val circle2 = BoundingBox.Circle(point = Point2D(90, 90), radius = 10)
  private val rect = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40)
  private val tri = BoundingBox.Triangle(point = Point2D(110, 110), height = 40)

  describe("Circle-Rectangle detection") {
    assert(Intersection.intersected(circle1, rect))
  }

  describe("Circle-Circle detection") {
    assert(Intersection.intersected(circle1, circle2))
  }

  describe("Circle-Triangle detection") {
    assert(Intersection.intersected(circle1, tri))
  }

  describe("Triangle-Triangle detection") {
    assert(!Intersection.intersected(tri, tri))
  }

  describe("Rectangle-Rectangle detection") {
    assert(!Intersection.intersected(rect, rect))
  }

  describe("Rectangle-Triangle detection") {
    assert(!Intersection.intersected(rect, tri))
  }
}