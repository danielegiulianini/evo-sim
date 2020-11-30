package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing.JSlider

/** A class that provides a monadic description of the operations supplied by Swing's [[JSlider]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jSlider that this class wraps.
 */
class JSliderIO(override val component: JSlider) extends JComponentIO(component){

  /** Returns an [[IO]] containing the description of a [[JSlider#setMinimum]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def minimumSet(min: Int): IO[Unit] = IO { component.setMinimum(min) }

  /** Returns an [[IO]] containing the description of a [[JSlider#setMaximum]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def maximumSet(max: Int): IO[Unit] = IO { component.setMaximum(max) }

  /** Returns an [[IO]] containing the description of a [[JSlider#setValue]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def valueSet(value: Int): IO[Unit] = IO { component.setValue(value) }

  /** Returns an [[IO]] containing the description of a [[JSlider#getValue]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def valueGot: IO[Int] = IO { component.getValue }

  /** Returns an [[IO]] containing the description of a [[JSlider#setMajorTickSpacing]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def majorTickSpacingSet(spacing: Int): IO[Unit] = IO { component.setMajorTickSpacing(spacing) }

  /** Returns an [[IO]] containing the description of a [[JSlider#setMinorTickSpacing]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def minorTickSpacingSet(spacing: Int): IO[Unit] = IO { component.setMinorTickSpacing(spacing) }

  /** Returns an [[IO]] containing the description of a [[JSlider#setPaintTicks]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def paintTicksSet(b: Boolean): IO[Unit] = IO { component.setPaintTicks(b) }

  /** Returns an [[IO]] containing the description of a [[JSlider#setPaintLabels]]
   * method invocation on the [[JSlider]] wrapped in this instance. */
  def paintLabelsSet(b: Boolean): IO[Unit] = IO { component.setPaintLabels(b) }

  //procedural event listener description:
  def changeListenerAdded(l: ChangeListener): IO[Unit] = IO {component.addChangeListener(l)}
  def changeListenerRemoved(l: ChangeListener): IO[Unit] = IO {component.removeChangeListener(l)}

  //monadic event listener description:
  def changeListenerAdded(l: MonadicChangeListener): IO[Unit] =
    IO { component.addChangeListener(l(_).unsafeRunSync())}
  def changeListenerAdded(l: => IO[Unit]): IO[Unit] =
    IO { component.addChangeListener(_ => l.unsafeRunSync()) }
}


/** A factory for [[IO]]s containing a JSliderIO instance.*/
object JSliderIO{
  def apply(): IO[JSliderIO] = IO { new JSliderIO(new JSlider) }
}