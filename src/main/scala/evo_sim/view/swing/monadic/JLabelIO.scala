package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.JLabel


/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JLabel]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jLabel that this class wraps.
 */
class JLabelIO(override val component: JLabel) extends JComponentIO[JLabel](component) {

  /** Returns an [[IO]] containing the description of a [[JLabel#setText]]
   * method invocation on the [[JLabel]] wrapped in this instance. */
  def textSet(text: String): IO[Unit] = IO {component.setText(text)}

  /** Returns an [[IO]] containing the description of a [[JLabel#getText]]
   * method invocation on the [[JLabel]] wrapped in this instance. */
  def textGot: IO[String] = IO {component.getText}
}

/** A factory for [[IO]]s containing a JLabelIO instance.*/
object JLabelIO{
  def apply(): IO[JLabelIO] = IO { new JLabelIO(new JLabel) }
  def apply(text:String): IO[JLabelIO] = IO { new JLabelIO(new JLabel(text)) }
}