package evo_sim.view.swing.monadic

import java.awt.event.{ActionEvent, ActionListener}

import cats.effect.IO
import javax.swing.JButton

class JButtonIO(override val component: JButton) extends ComponentIO(component){
  def actionListenerAdded(l:ActionListener) = IO {component.addActionListener(l)}
  def actionListenerRemoved(l:ActionListener) = IO {component.removeActionListener(l)}
  def textSet(text: String) = IO {component.setText(text)}
  def textGot() = IO {component.getText}
  def enabledSet(b: Boolean): IO[Unit] = IO { component.setEnabled(b) }

  //enabling event listener description by monad
  def actionListenerAdded(l:ActionEvent => IO[Unit]) = IO {
    component.addActionListener( e => l(e).unsafeRunSync() )
  }
}

//companion object with utilities
object JButtonIO {
  def apply(text:String) = IO { new JButtonIO(new JButton(text)) }
}
