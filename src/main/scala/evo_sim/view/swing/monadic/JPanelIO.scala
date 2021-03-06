package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.JPanel
import javax.swing.plaf.PanelUI

/** A class that provides a monadic description of the operations supplied by Swing's [[JPanel]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jPanel that this class wraps.
 */
class JPanelIO (override val component: JPanel) extends JComponentIO(component) {

  /** Returns an [[IO]] containing the description of a [[JPanel#setUI]]
   * method invocation on the [[JPanel]] wrapped in this instance. */
  def uiSet(ui:PanelUI): IO[Unit] = IO {	component.setUI(ui)}

  /** Returns an [[IO]] containing the description of a [[JPanel#getUI]]
   * method invocation on the [[JPanel]] wrapped in this instance. */
  def uiGot(): IO[PanelUI] = IO {component.getUI}
}


/** A factory for [[IO]]s containing a JPanelIO instance.*/
object JPanelIO{
  def apply(): IO[JPanelIO] = IO { new JPanelIO(new JPanel) }
}