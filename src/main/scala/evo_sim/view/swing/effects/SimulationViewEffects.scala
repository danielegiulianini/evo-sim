package evo_sim.view.swing.effects

import java.awt.{Color, Graphics}

import cats.effect.IO
import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus}
import evo_sim.model.{BoundingBox, Intersection, Point2D, World}
import evo_sim.view.swing.ShapesPanel

object SimulationViewEffects {

  private val fieldOfViewColor = new Color(255, 255, 0)

  def shapesPanelCreated(world: World): IO[ShapesPanel] = IO pure new ShapesPanel(world)

  def drawBlobs(g: Graphics, world: World, viewWidth: Int, viewHeight: Int): IO[Unit] =
    IO apply world.entities.foreach(e => {
      e match {
        case b: Blob => (for {
          _ <- IO apply g.setColor(fieldOfViewColor)
          x <- modelToViewRatio(e.boundingBox.point.x - b.fieldOfViewRadius, viewWidth,
            world.width)
          y <- modelToViewRatio(e.boundingBox.point.y - b.fieldOfViewRadius, viewHeight,
            world.height)
          width <- modelToViewRatio(b.fieldOfViewRadius * 2, viewWidth, world.width)
          height <- modelToViewRatio(b.fieldOfViewRadius * 2, viewHeight, world.height)
          _ <- IO apply g.drawOval(x, y, width, height)
          _ <- IO apply world.entities.filter(e2 => Intersection.intersected(Circle(b.boundingBox.point,
            b.fieldOfViewRadius), e2.boundingBox)).foreach(e2 =>
            drawFoodOrObstacle(g, e2.boundingBox, width, height, world.width, world.height))
        } yield ()).unsafeRunSync()
        case tb: BlobWithTemporaryStatus => (for {
          _ <- IO apply g.setColor(fieldOfViewColor)
          x <- modelToViewRatio(e.boundingBox.point.x - tb.blob.fieldOfViewRadius,
            viewWidth, world.width)
          y <- modelToViewRatio(e.boundingBox.point.y - tb.blob.fieldOfViewRadius,
            viewHeight, world.height)
          width <- modelToViewRatio(tb.blob.fieldOfViewRadius * 2, viewWidth,
            world.width)
          height <- modelToViewRatio(tb.blob.fieldOfViewRadius * 2, viewHeight, world.height)
          _ <- IO apply g.drawOval(x, y, width, height)
          _ <- IO apply world.entities.filter(e2 => Intersection.intersected(Circle(tb.blob.boundingBox.point,
            tb.blob.fieldOfViewRadius), e2.boundingBox)).foreach(e2 =>
            drawFoodOrObstacle(g, e2.boundingBox, width, height, world.width, world.height))
        } yield ()).unsafeRunSync()
        case _ =>
      }
      e.boundingBox match {
        case Circle(point2D, r) => (for {
          _ <- IO apply g.setColor(Color.blue)
          x <- modelToViewRatio(point2D.x - r, viewWidth, world.width)
          y <- modelToViewRatio(point2D.y - r, viewHeight, world.height)
          width <- modelToViewRatio(r * 2, viewWidth, world.width)
          height <- modelToViewRatio(r * 2, viewHeight, world.height)
          _ <- IO apply g.fillOval(x, y, width, height)
        } yield ()).unsafeRunSync()
        case _ =>
      }
    })

  def drawFoodOrObstacle(g: Graphics, boundingBox: BoundingBox, viewWidth: Int, viewHeight: Int,
                         worldWidth: Int, worldHeight: Int): IO[Unit] = IO apply {
    boundingBox match {
      case Rectangle(point2D, w, h) => (for {
        _ <- IO apply g.setColor(Color.red)
        x <- modelToViewRatio(point2D.x - w / 2, viewWidth, viewWidth)
        y <- modelToViewRatio(point2D.y - h / 2, viewHeight, viewHeight)
        width <- modelToViewRatio(w, viewWidth, worldWidth)
        height <- modelToViewRatio(h, viewHeight, worldHeight)
        _ <- IO apply g.fillRect(x, y, width, height)
      } yield ()).unsafeRunSync()
      case Triangle(point2D, h, a) => (for {
        vertices <- triangleVertices(Triangle(point2D, h, a))
        _ <- IO apply g.setColor(Color.green)
        xPoints <- IO pure vertices.productIterator.map({
          case p: Point2D =>
            modelToViewRatio(p.x, viewWidth, worldWidth).unsafeRunSync()
        }).toArray
        yPoints <- IO pure vertices.productIterator.map({
          case p: Point2D =>
            modelToViewRatio(p.y, viewHeight, worldHeight).unsafeRunSync()
        }).toArray
        nPoints <- IO pure vertices.productIterator.length
        _ <- IO apply g.fillPolygon(xPoints, yPoints, nPoints)
      } yield ()).unsafeRunSync()
      case _ =>
    }
  }

  /** return a tuple3 with the vertices
   * v1 = center.x, center.y  + radius           -> upper vertices
   * v2 = center.x - radius, center.y  - radius  -> bottom left vertices
   * v1 = center.x + radius, center.y  - radius  -> bottom right vertices
   */
  def triangleVertices(tri: Triangle): IO[(Point2D, Point2D, Point2D)] = {
    val radius = tri.height / 3 * 2
    IO pure(Point2D(tri.point.x, tri.point.y + radius), Point2D(tri.point.x - radius, tri.point.y - radius),
      Point2D(tri.point.x + radius, tri.point.y - radius))
  }

  def modelToViewRatio(modelProperty: Int, viewDimension: Int, modelDimension: Int): IO[Int] =
    IO pure modelProperty * viewDimension / modelDimension

}
