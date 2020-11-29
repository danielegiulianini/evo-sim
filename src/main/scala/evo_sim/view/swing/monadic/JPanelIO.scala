package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.JPanel
import javax.swing.plaf.PanelUI

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JPanel]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jPanel that this class wraps.
 */
class JPanelIO (override val component: JPanel) extends JComponentIO(component) {
  def uiSet(ui:PanelUI): IO[Unit] = IO {	component.setUI(ui)}
  def uiGot(): IO[PanelUI] = IO {component.getUI}
}


/** Factory for JPanelIO instances*/
object JPanelIO{
  def apply(): IO[JPanelIO] = IO { new JPanelIO(new JPanel) }
}