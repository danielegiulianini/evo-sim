package evo_sim.view.swing

import java.awt.{Color, Dimension, Graphics}

import cats.effect.IO
import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.Constants._
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, PoisonousPlant, ReproducingPlant, SlowBlob, StandardPlant}
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus, Entity, Obstacle, Plant}
import evo_sim.model.{BoundingBox, Intersection, Point2D, World}
import javax.swing.JPanel

class ShapesPanel(world: World) extends JPanel {

  override def paintComponent(g: Graphics): Unit = (for {
    _ <- IO { super.paintComponent(g) }
    // draw temperature filter
    red <- modelToViewRatio(world.temperature - MIN_TEMPERATURE, 255, MAX_TEMPERATURE - MIN_TEMPERATURE)
    maxBlue <- IO pure 255
    notBlue <- modelToViewRatio(world.temperature - MIN_TEMPERATURE, 255,
      MAX_TEMPERATURE - MIN_TEMPERATURE)
    blue <- IO { maxBlue - notBlue }
    temperatureColor <- IO pure new Color(red, 0, blue, 75)
    _ <- IO { g.setColor(temperatureColor) }
    _ <- IO { g.fillRect(0, 0, getWidth, getHeight) }
    // draw rectangles and triangles before transparent filter and circles
    _ <- IO { world.entities.foreach(e =>
      drawFoodsObstaclesAndPlants(g, e, getWidth, getHeight, world.width, world.height).unsafeRunSync()) }
    // draw luminosity filter
    maxAlpha <- IO pure 255
    notAlpha <- modelToViewRatio(world.luminosity, 255, MAX_LUMINOSITY)
    alpha <- IO { maxAlpha - notAlpha.max(0).min(255) }
    luminosityColor <- IO pure new Color(0, 0, 0, alpha)
    _ <- IO { g.setColor(luminosityColor) }
    _ <- IO { g.fillRect(0, 0, getWidth, getHeight) }
    // draw blob circles with field of view
    _ <- drawBlobs(g, world, getWidth, getHeight)
  } yield ()).unsafeRunSync()

  override def getPreferredSize: Dimension = {
    val borderValue = 15
    new Dimension(getParent.getSize().width - borderValue, getParent.getSize().height - borderValue)
  }

  private val fieldOfViewColor = new Color(255, 255, 0)

  def shapesPanelCreated(world: World): IO[ShapesPanel] = IO pure new ShapesPanel(world)

  def drawFieldOfView(g: Graphics, b: Blob, world: World, viewWidth: Int, viewHeight: Int): IO[(Int, Int)] = for {
    _ <- IO { g.setColor(fieldOfViewColor) }
    x <- modelToViewRatio(b.boundingBox.point.x - b.fieldOfViewRadius, viewWidth,
      world.width)
    y <- modelToViewRatio(b.boundingBox.point.y - b.fieldOfViewRadius, viewHeight,
      world.height)
    width <- modelToViewRatio(b.fieldOfViewRadius * 2, viewWidth, world.width)
    height <- modelToViewRatio(b.fieldOfViewRadius * 2, viewHeight, world.height)
    _ <- IO { g.drawOval(x, y, width, height) }
    _ <- IO { world.entities.filter(e2 => Intersection.intersected(Circle(b.boundingBox.point,
      b.fieldOfViewRadius), e2.boundingBox)).foreach(e2 =>
      drawFoodsObstaclesAndPlants(g, e2, width, height, world.width, world.height)) }
  } yield (width, height)

  def drawBlobs(g: Graphics, world: World, viewWidth: Int, viewHeight: Int): IO[Unit] =
    IO {
      world.entities.foreach(e => {
        e match {
          case b: Blob => (for {
            _ <- drawFieldOfView(g, b, world, viewWidth, viewHeight)
          } yield ()).unsafeRunSync()
          case _ =>
        }
        e.boundingBox match {
          case Circle(point2D, r) => (for {
            _ <- e match {
              case _ : BaseBlob => IO { g.setColor(Color.blue) }
              case _ : CannibalBlob => IO { g.setColor(Color.red) }
              case _ : SlowBlob => IO { g.setColor(Color.darkGray) }
              case _ : PoisonBlob => IO { g.setColor(new Color(128, 0, 128)) } // purple
              case _ => IO { g.setColor(Color.black) }
            }
            x <- modelToViewRatio(point2D.x - r, viewWidth, world.width)
            y <- modelToViewRatio(point2D.y - r, viewHeight, world.height)
            width <- modelToViewRatio(r * 2, viewWidth, world.width)
            height <- modelToViewRatio(r * 2, viewHeight, world.height)
            _ <- IO { g.fillOval(x, y, width, height) }
          } yield ()).unsafeRunSync()
          case _ =>
        }
      })
    }

  def drawFoodsObstaclesAndPlants(g: Graphics, entity: Entity, viewWidth: Int, viewHeight: Int,
                                  worldWidth: Int, worldHeight: Int): IO[Unit] = IO {
    entity.boundingBox match {
      case Rectangle(point2D, w, h) => (for {
        _ <- entity match {
          case _ : Obstacle => IO { g.setColor(Color.red) }
          case _ : StandardPlant => IO { g.setColor(Color.orange) }
          case _ : ReproducingPlant => IO { g.setColor(Color.magenta) }
          case _ : PoisonousPlant => IO { g.setColor(Color.pink) }
          case _ => IO { g.setColor(Color.black) }
        }
        x <- modelToViewRatio(point2D.x - w / 2, viewWidth, viewWidth)
        y <- modelToViewRatio(point2D.y - h / 2, viewHeight, viewHeight)
        width <- modelToViewRatio(w, viewWidth, worldWidth)
        height <- modelToViewRatio(h, viewHeight, worldHeight)
        _ <- IO { g.fillRect(x, y, width, height) }
      } yield ()).unsafeRunSync()
      case Triangle(point2D, h, a) => (for {
        vertices <- triangleVertices(Triangle(point2D, h, a))
        _ <- IO { g.setColor(Color.green) }
        xPoints <- IO pure vertices.productIterator.map({
          case p: Point2D =>
            modelToViewRatio(p.x, viewWidth, worldWidth).unsafeRunSync()
        }).toArray
        yPoints <- IO pure vertices.productIterator.map({
          case p: Point2D =>
            modelToViewRatio(p.y, viewHeight, worldHeight).unsafeRunSync()
        }).toArray
        nPoints <- IO pure vertices.productIterator.length
        _ <- IO { g.fillPolygon(xPoints, yPoints, nPoints) }
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
