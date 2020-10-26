package evo_sim.view.swing.monadic

import java.awt.event.ActionListener

import cats.effect.IO
import javax.swing.JButton

class JButtonIO(jButton: JButton) extends ComponentIO(jButton){
  def actionListenerAdded(l:ActionListener) = IO {jButton.addActionListener(l)}
  def actionListenerRemoved(l:ActionListener) = IO {jButton.removeActionListener(l)}
  def textSet(text: String) = IO {jButton.setText(text)}
  def textGot() = IO {jButton.getText}
}

//companion object with utilities to be added
object JButtonIO {
  def apply(text:String) = IO { new JButtonIO(new JButton(text)) }
}
