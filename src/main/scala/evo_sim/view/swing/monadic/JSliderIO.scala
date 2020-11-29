package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing.JSlider

/** A class that provides a monadic description of the operations supplied by Swing's [[JSlider]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jSlider that this class wraps.
 */
class JSliderIO(override val component: JSlider) extends JComponentIO(component){
  def minimumSet(min: Int): IO[Unit] = IO { component.setMinimum(min) }
  def maximumSet(max: Int): IO[Unit] = IO { component.setMaximum(max) }
  def valueSet(value: Int): IO[Unit] = IO { component.setValue(value) }
  def valueGot: IO[Int] = IO { component.getValue }
  def majorTickSpacingSet(spacing: Int): IO[Unit] = IO { component.setMajorTickSpacing(spacing) }
  def minorTickSpacingSet(spacing: Int): IO[Unit] = IO { component.setMinorTickSpacing(spacing) }
  def paintTicksSet(b: Boolean): IO[Unit] = IO { component.setPaintTicks(b) }
  def paintLabelsSet(b: Boolean): IO[Unit] = IO { component.setPaintLabels(b) }

  //procedural event listener description:
  def changeListenerAdded(l: ChangeListener): IO[Unit] = IO {component.addChangeListener(l)}
  def changeListenerRemoved(l: ChangeListener): IO[Unit] = IO {component.removeChangeListener(l)}

  //monadic event listener description:
  def changeListenerAdded(l: ChangeEvent => IO[Unit]): IO[Unit] =
    IO { component.addChangeListener(l(_).unsafeRunSync())}
  def changeListenerAdded(l: => IO[Unit]): IO[Unit] =
    IO { component.addChangeListener(_ => l.unsafeRunSync()) }
}


/** A factory for [[IO]]s containing a JSliderIO instance.*/
object JSliderIO{
  def apply(): IO[JSliderIO] = IO { new JSliderIO(new JSlider) }
}