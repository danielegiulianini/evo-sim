package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.JLabel

class JLabelIO(override val component: JLabel) extends JComponentIO[JLabel](component) {
  def textSet(text: String) = IO {component.setText(text)}
  def textGot() = IO {component.getText}
}

//companion object with utilities
object JLabelIO{
  def apply() = IO { new JLabelIO(new JLabel) }
  def apply(text:String) = IO { new JLabelIO(new JLabel(text)) }
}