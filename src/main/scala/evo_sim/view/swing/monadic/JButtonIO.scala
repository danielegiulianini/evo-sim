package evo_sim.view.swing.monadic

import java.awt.event.{ActionListener}

import cats.effect.IO
import javax.swing.JButton

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JButton]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jButton that this class wraps.
 */
class JButtonIO(override val component: JButton) extends ComponentIO(component){
  def textGot(): IO[String] = IO {component.getText}
  def textSet(text: String): IO[Unit] = IO {component.setText(text)}
  def enabledSet(b: Boolean): IO[Unit] = IO { component.setEnabled(b) }

  //procedural event listener description (from API-user point of view)
  def actionListenerAdded(l: ActionListener): IO[Unit] = IO {component.addActionListener(l)}
  def actionListenerRemoved(l:ActionListener): IO[Unit] = IO {component.removeActionListener(l)}

  //monadic event listener description
  def monadicActionListenerAdded(l:MonadicActionListener): IO[Unit] =
    IO {component.addActionListener(l(_).unsafeRunSync())}
  def monadicActionListenerRemoved(l:MonadicActionListener): IO[Unit] =
    IO {component.removeActionListener(l(_).unsafeRunSync())}
}

/** Factory for JButtonIO instances*/
object JButtonIO {
  def apply(text:String): IO[JButtonIO] = IO { new JButtonIO(new JButton(text)) }
}
