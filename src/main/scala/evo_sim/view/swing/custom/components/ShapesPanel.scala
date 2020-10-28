package evo_sim.view.swing.custom.components

import java.awt.{Color, Dimension, Graphics}

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.Constants._
import evo_sim.model.Entities._
import evo_sim.model.EntityStructure.{Blob, Entity, Obstacle}
import evo_sim.model.{Intersection, Point2D, World}
import javax.swing.JPanel

class ShapesPanel(world: World) extends JPanel {

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)

    // draw temperature filter
    val redIntensity = modelToViewRatio(world.temperature - MIN_TEMPERATURE, 255, MAX_TEMPERATURE - MIN_TEMPERATURE)
    val blueIntensity = 255 - redIntensity
    val temperatureColor = new Color(redIntensity, 0, blueIntensity, 75)
    g.setColor(temperatureColor)
    g.fillRect(0, 0, getWidth, getHeight)

    // draw rectangles and triangles before the transparent filter and circles
    world.entities.foreach(e =>
      drawFoodsObstaclesAndPlants(g, e, getWidth, getHeight, world.width, world.height))

    // draw luminosity filter
    val alpha = 255 - modelToViewRatio(world.luminosity, 255, MAX_LUMINOSITY).max(0).min(255)
    val luminosityColor = new Color(0, 0, 0, alpha)
    g.setColor(luminosityColor)
    g.fillRect(0, 0, getWidth, getHeight)

    // draw blob circles with the field of view
    drawBlobs(g, world, getWidth, getHeight)
  }

  override def getPreferredSize: Dimension = {
    val borderValue = 15
    new Dimension(getParent.getSize().width - borderValue, getParent.getSize().height - borderValue)
  }

  private def drawFieldOfView(g: Graphics, b: Blob, world: World, viewWidth: Int, viewHeight: Int): Unit = {
    g.setColor(new Color(255, 255, 0))
    val x = modelToViewRatio(b.boundingBox.point.x - b.fieldOfViewRadius, viewWidth, world.width)
    val y = modelToViewRatio(b.boundingBox.point.y - b.fieldOfViewRadius, viewHeight, world.height)
    val width = modelToViewRatio(b.fieldOfViewRadius * 2, viewWidth, world.width)
    val height = modelToViewRatio(b.fieldOfViewRadius * 2, viewHeight, world.height)
    g.drawOval(x, y, width, height)
    world.entities.filter(e2 => Intersection.intersected(Circle(b.boundingBox.point,
      b.fieldOfViewRadius), e2.boundingBox)).foreach(e2 =>
      drawFoodsObstaclesAndPlants(g, e2, width, height, world.width, world.height))
  }

  private def drawBlobs(g: Graphics, world: World, viewWidth: Int, viewHeight: Int): Unit = {
    world.entities.foreach(e => {
      e match {
        case b: Blob => drawFieldOfView(g, b, world, viewWidth, viewHeight)
        case _ =>
      }
      e.boundingBox match {
        case Circle(point2D, r) =>
          e match {
            case _: BaseBlob => g.setColor(Color.BLUE)
            case _: CannibalBlob => g.setColor(Color.RED)
            case _: SlowBlob => g.setColor(Color.DARK_GRAY)
            case _: PoisonBlob => g.setColor(Color.MAGENTA)
            case _ => g.setColor(Color.BLACK)
          }
          val x = modelToViewRatio(point2D.x - r, viewWidth, world.width)
          val y = modelToViewRatio(point2D.y - r, viewHeight, world.height)
          val width = modelToViewRatio(r * 2, viewWidth, world.width)
          val height = modelToViewRatio(r * 2, viewHeight, world.height)
          g.fillOval(x, y, width, height)
        case _ =>
      }
    })
  }

  private def drawFoodsObstaclesAndPlants(g: Graphics, entity: Entity, viewWidth: Int, viewHeight: Int,
                                          worldWidth: Int, worldHeight: Int): Unit = {
    entity.boundingBox match {
      case Rectangle(point2D, w, h) =>
        entity match {
          case _: Obstacle => g.setColor(Color.RED)
          case _: StandardPlant => g.setColor(Color.GREEN)
          case _: ReproducingPlant => g.setColor(Color.PINK)
          case _: PoisonousPlant => g.setColor(Color.MAGENTA)
          case _ => g.setColor(Color.black)
        }
        val x = modelToViewRatio(point2D.x - w / 2, viewWidth, viewWidth)
        val y = modelToViewRatio(point2D.y - h / 2, viewHeight, viewHeight)
        val width = modelToViewRatio(w, viewWidth, worldWidth)
        val height = modelToViewRatio(h, viewHeight, worldHeight)
        g.fillRect(x, y, width, height)
      case Triangle(point2D, h, a) =>
        val vertices = triangleVertices(Triangle(point2D, h, a))
        g.setColor(Color.green)
        val xPoints = vertices.productIterator.map({
          case p: Point2D =>
            modelToViewRatio(p.x, viewWidth, worldWidth)
        }).toArray
        val yPoints = vertices.productIterator.map({
          case p: Point2D =>
            modelToViewRatio(p.y, viewHeight, worldHeight)
        }).toArray
        val nPoints = vertices.productIterator.length
        g.fillPolygon(xPoints, yPoints, nPoints)
      case _ =>
    }
  }

  /** Return a tuple3 with the vertices
   * v1 = center.x, center.y  + radius           -> upper vertices
   * v2 = center.x - radius, center.y  - radius  -> bottom left vertices
   * v1 = center.x + radius, center.y  - radius  -> bottom right vertices
   */
  private def triangleVertices(tri: Triangle): (Point2D, Point2D, Point2D) = {
    val radius = tri.height / 3 * 2
    (Point2D(tri.point.x, tri.point.y + radius), Point2D(tri.point.x - radius, tri.point.y - radius),
      Point2D(tri.point.x + radius, tri.point.y - radius))
  }

  private def modelToViewRatio(modelProperty: Int, viewDimension: Int, modelDimension: Int): Int =
    modelProperty * viewDimension / modelDimension

}
