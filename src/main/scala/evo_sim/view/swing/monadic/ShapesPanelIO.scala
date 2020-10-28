package evo_sim.view.swing.monadic

import cats.effect.IO
import evo_sim.model.World
import evo_sim.view.swing.custom.components.ShapesPanel
import javax.swing.JPanel

class ShapesPanelIO (val shapesPanel: JPanel) extends JPanelIO(shapesPanel) {
}

//companion object with utilities
object ShapesPanelIO {
  def apply(world: World): IO[ShapesPanelIO] = IO { new ShapesPanelIO(new ShapesPanel(world)) }
}