package evo_sim.view.swing

import java.awt.{Color, Dimension, Graphics}

import cats.effect.IO
import evo_sim.model.Constants._
import evo_sim.model.World
import evo_sim.view.swing.effects.SimulationViewEffects._
import javax.swing.JPanel

class ShapesPanel(world: World) extends JPanel {

  override def paintComponent(g: Graphics): Unit = (for {
    _ <- IO apply super.paintComponent(g)
    // draw temperature filter
    red <- modelToViewRatio(world.temperature - MIN_TEMPERATURE, 255, MAX_TEMPERATURE - MIN_TEMPERATURE)
    maxBlue <- IO pure 255
    notBlue <- modelToViewRatio(world.temperature - MIN_TEMPERATURE, 255,
      MAX_TEMPERATURE - MIN_TEMPERATURE)
    blue <- IO apply maxBlue - notBlue
    temperatureColor <- IO pure new Color(red, 0, blue, 75)
    _ <- IO apply g.setColor(temperatureColor)
    _ <- IO apply g.fillRect(0, 0, getWidth, getHeight)
    // draw rectangles and triangles before transparent filter and circles
    _ <- IO apply world.entities.foreach(e =>
      drawFoodOrObstacle(g, e.boundingBox, getWidth, getHeight, world.width, world.height).unsafeRunSync())
    // draw luminosity filter
    maxAlpha <- IO pure 255
    notAlpha <- modelToViewRatio(world.luminosity, 255, MAX_LUMINOSITY)
    alpha <- IO apply maxAlpha - notAlpha.max(0).min(255)
    luminosityColor <- IO pure new Color(0, 0, 0, alpha)
    _ <- IO apply g.setColor(luminosityColor)
    _ <- IO apply g.fillRect(0, 0, getWidth, getHeight)
    // draw blob circles with field of view
    _ <- drawBlobs(g, world, getWidth, getHeight)
  } yield ()).unsafeRunSync()

  override def getPreferredSize: Dimension = {
    val borderValue = 15
    new Dimension(getParent.getSize().width - borderValue, getParent.getSize().height - borderValue)
  }

}
