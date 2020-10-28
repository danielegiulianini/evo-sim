import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle, CannibalBlob}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.Utils.chain
import evo_sim.model._
import org.scalatest.FunSuite

class IntersectionTests extends FunSuite {

  val INIT_BLOB_LIFE = 100
  val INIT_BLOB_VEL = 50

  val circle1 = BoundingBox.Circle(point = Point2D(100, 100), radius = 10)

  val circle2 = BoundingBox.Circle(point = Point2D(90, 90), radius = 10)

  val rect = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40)

  val tri = BoundingBox.Triangle(point = Point2D(110, 110), height = 40)

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