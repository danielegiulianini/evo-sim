package evo_sim.view.swing.monadic

import java.awt.event.{ActionEvent, ActionListener}

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

  type MonadicActionListener = ActionEvent => IO[Unit]

  //procedural event listener description (from API-user point of view)
  def actionListenerAdded(l:ActionListener): IO[Unit] = IO {component.addActionListener(l)}
  //event listener that doesn't leverage action event parameter
  def actionListenerAddedFromUnit(l: => Unit): IO[Unit] = IO {component.addActionListener(_ => l)}
  //monadic event listener description
  def actionListenerAdded(l:MonadicActionListener): IO[Unit] =
    IO {component.addActionListener( e => l(e).unsafeRunSync() )}
  //event listener that doesn't leverage action event parameter
  def actionListenerAdded(l: => IO[Unit]): IO[Unit] =
    IO {component.addActionListener( _ => l.unsafeRunSync() )}

  def actionListenerRemoved(l:ActionListener): IO[Unit] = IO {component.removeActionListener(l)}

  /*def textSetInvokingAndWaiting(text: String): IO[Unit] = invokeAndWaitIO(component.setText(text))
  def enabledSetInvokingAndWaiting(b: Boolean): IO[Unit] = invokeAndWaitIO(component.setEnabled(b))*/
}

/** Factory for JButtonIO instances*/
object JButtonIO {
  def apply(text:String): IO[JButtonIO] = IO { new JButtonIO(new JButton(text)) }
}
