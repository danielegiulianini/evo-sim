package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.event.ChangeListener
import javax.swing.{JComponent, JSlider}

class JSliderIO(jSlider: JSlider) extends JComponentIO(jSlider){
  def changeListenerAdded(l: ChangeListener) = IO {jSlider.addChangeListener(l)}
  def changeListenerRemoved(l: ChangeListener) = IO {jSlider.addChangeListener(l)}
  def minimumSet(min: Int) = IO { jSlider.setMinimum(min) }
  def maximumSet(max: Int) = IO { jSlider.setMinimum(max) }
  def valueSet(value: Int): IO[Unit] = IO {jSlider.setValue(value)}
  def valueGot: IO[Int] = IO {jSlider.getValue}
}


//companion object with utilities
object JSliderIO{
  def apply() = IO { new JSliderIO(new JSlider) }
}