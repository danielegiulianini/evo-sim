package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.{JPanel}

class JPanelIO (jPanel: JPanel) extends ComponentIO(jPanel) {
}

//companion object with utilities to be added
object JPanelIO{
  def apply() = IO { new JPanelIO(new JPanel) }
}