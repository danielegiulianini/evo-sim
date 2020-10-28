package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.{JPanel}

class JPanelIO (val jPanel: JPanel) extends JComponentIO(jPanel) {
}

//companion object with utilities
object JPanelIO{
  def apply() = IO { new JPanelIO(new JPanel) }
}