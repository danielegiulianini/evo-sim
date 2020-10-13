package evo_sim.view.swing

import java.awt.{Color, Dimension, Graphics}

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.{Point2D, World}
import javax.swing.JPanel

class ShapesPanel(world: World) extends JPanel {

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    world.entities.foreach(e => e.boundingBox match {
      case Circle(point2D, r) =>
        g.setColor(Color.blue)
        g.fillOval(modelToViewRatio(point2D.x - r, this.getSize().width, world.width),
          modelToViewRatio(point2D.y - r, this.getSize().height, world.height),
          modelToViewRatio(r * 2, this.getSize().width, world.width),
          modelToViewRatio(r * 2, this.getSize().height, world.height))
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

}
