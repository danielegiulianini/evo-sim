package evo_sim.view.swing

import java.awt.{Color, Dimension, Graphics, Toolkit}

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.{BoundingBox, Intersection, Point2D, World}
import evo_sim.model.Constants._
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus}
import javax.swing.JPanel

class ShapesPanel(world: World) extends JPanel {

  private val fieldOfViewColor = new Color(255, 255, 125)

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    println("lum"+ world.luminosity)

    // draw rectangles and triangles before transparent filter and circles
    world.entities.foreach(e => drawRectangleOrTriangle(g, e.boundingBox))

    // draw luminosity filter
    g.setColor(new Color(0, 0, 0, 255 - modelToViewRatio(world.luminosity, 255, MAX_LUMINOSITY).max(0).min(255)))
    g.fillRect(0, 0, modelToViewRatio(Toolkit.getDefaultToolkit.getScreenSize.width, this.getSize().width, world.width), modelToViewRatio(Toolkit.getDefaultToolkit.getScreenSize.height, this.getSize().width, world.width))

    // draw blob circles with field of view
    world.entities.foreach(e => {
      e match {
        case b : Blob =>
          g.setColor(fieldOfViewColor)
          g.fillOval(modelToViewRatio(e.boundingBox.point.x - b.fieldOfViewRadius, this.getSize().width, world.width),
            modelToViewRatio(e.boundingBox.point.y - b.fieldOfViewRadius, this.getSize().height, world.height),
            modelToViewRatio(b.fieldOfViewRadius * 2, this.getSize().width, world.width),
            modelToViewRatio(b.fieldOfViewRadius * 2, this.getSize().height, world.height))
          world.entities.filter(e2 => Intersection.intersected(Circle(b.boundingBox.point, b.fieldOfViewRadius), e2.boundingBox))
            .foreach(e2 => drawRectangleOrTriangle(g, e2.boundingBox))
        case tb : BlobWithTemporaryStatus =>
          g.setColor(fieldOfViewColor)
          g.fillOval(modelToViewRatio(e.boundingBox.point.x - tb.blob.fieldOfViewRadius, this.getSize().width, world.width),
            modelToViewRatio(e.boundingBox.point.y - tb.blob.fieldOfViewRadius, this.getSize().height, world.height),
            modelToViewRatio(tb.blob.fieldOfViewRadius * 2, this.getSize().width, world.width),
            modelToViewRatio(tb.blob.fieldOfViewRadius * 2, this.getSize().height, world.height))
          world.entities.filter(e2 => Intersection.intersected(Circle(tb.blob.boundingBox.point, tb.blob.fieldOfViewRadius), e2.boundingBox))
            .foreach(e2 => drawRectangleOrTriangle(g, e2.boundingBox))
        case _ =>
      }
      e.boundingBox match {
      case Circle(point2D, r) =>
        g.setColor(Color.blue)
        g.fillOval(modelToViewRatio(point2D.x - r, this.getSize().width, world.width),
          modelToViewRatio(point2D.y - r, this.getSize().height, world.height),
          modelToViewRatio(r * 2, this.getSize().width, world.width),
          modelToViewRatio(r * 2, this.getSize().height, world.height))
      case _ =>
      }
    })
  }

  override def getPreferredSize = new Dimension(this.getParent.getSize())

  private def triangleVertices(tri: Triangle) = {
    /** return a tuple3 with the vertices
     *  v1 = center.x, center.y  + radius           -> upper vertices
     *  v2 = center.x - radius, center.y  - radius  -> bottom left vertices
     *  v1 = center.x + radius, center.y  - radius  -> bottom right vertices
     */
    val radius = tri.height/3*2
    (Point2D(tri.point.x, tri.point.y + radius), Point2D(tri.point.x - radius, tri.point.y - radius), Point2D(tri.point.x + radius, tri.point.y - radius))
  }

  private def modelToViewRatio(modelProperty: Int, viewDimension: Int, modelDimension: Int): Int = {
    modelProperty * viewDimension / modelDimension
  }

  private def drawRectangleOrTriangle(g: Graphics, boundingBox: BoundingBox): Unit = {
    boundingBox match {
      case Rectangle(point2D, w, h) =>
        g.setColor(Color.red)
        g.fillRect(modelToViewRatio(point2D.x - w / 2, this.getSize().width, world.width),
          modelToViewRatio(point2D.y - h / 2, this.getSize().height, world.height),
          modelToViewRatio(w, this.getSize().width, world.width),
          modelToViewRatio(h, this.getSize().height, world.height))
      case Triangle(point2D, h, a) =>
        val vertices = triangleVertices(Triangle(point2D, h, a))
        g.setColor(Color.green)
        g.fillPolygon(vertices.productIterator.map({
          case p: Point2D => modelToViewRatio(p.x, this.getSize().width, world.width)
        }).toArray, vertices.productIterator.map({
          case p: Point2D => modelToViewRatio(p.y, this.getSize().height, world.height)
        }).toArray, vertices.productIterator.length)
      case _ =>
    }
  }

}
