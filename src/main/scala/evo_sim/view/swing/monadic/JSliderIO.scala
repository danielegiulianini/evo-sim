package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing.{JComponent, JSlider}

class JSliderIO(override val component: JSlider) extends JComponentIO(component){
  def changeListenerAdded(l: ChangeListener) = IO {component.addChangeListener(l)}
  def changeListenerRemoved(l: ChangeListener) = IO {component.removeChangeListener(l)}
  def minimumSet(min: Int) = IO { component.setMinimum(min) }
  def maximumSet(max: Int) = IO { component.setMaximum(max) }
  def valueSet(value: Int): IO[Unit] = IO { component.setValue(value) }
  def valueGot: IO[Int] = IO { component.getValue }
  def majorTickSpacingSet(spacing: Int): IO[Unit] = IO { component.setMajorTickSpacing(spacing) }
  def minorTickSpacingSet(spacing: Int): IO[Unit] = IO { component.setMinorTickSpacing(spacing) }
  def paintTicksSet(b: Boolean): IO[Unit] = IO { component.setPaintTicks(b) }
  def paintLabelsSet(b: Boolean): IO[Unit] = IO { component.setPaintLabels(b) }

  def actionListenerAdded(l:ChangeEvent => IO[Unit]) =
    IO {component.addChangeListener( e => l(e).unsafeRunSync() )}
  //event listener that doesn't leverage action event parameter
  def actionListenerAdded(l: => IO[Unit]) =
    IO {component.addChangeListener( _ => l.unsafeRunSync() )}
}


//companion object with utilities
object JSliderIO{
  def apply() = IO { new JSliderIO(new JSlider) }
}