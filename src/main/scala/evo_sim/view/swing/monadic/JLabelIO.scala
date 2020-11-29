package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.JLabel


/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JLabel]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jLabel that this class wraps.
 */
class JLabelIO(override val component: JLabel) extends JComponentIO[JLabel](component) {
  def textSet(text: String): IO[Unit] = IO {component.setText(text)}
  def textGot: IO[String] = IO {component.getText}
}

/** Factory for JLabelIO instances*/
object JLabelIO{
  def apply(): IO[JLabelIO] = IO { new JLabelIO(new JLabel) }
  def apply(text:String): IO[JLabelIO] = IO { new JLabelIO(new JLabel(text)) }
}