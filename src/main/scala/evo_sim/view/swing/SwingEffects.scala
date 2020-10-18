package evo_sim.view.swing

import java.awt.BorderLayout

import cats.effect.IO
import javax.swing._

object SwingEffects {

  // (generic) component-related
  def componentBorderSet(component: JComponent, top: Int, left: Int, bottom: Int, right: Int): IO[Unit] =
    IO { component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right)) }

  // panel-related
  def panelCreated: IO[JPanel] = IO { new JPanel }

  def panelLayoutSet(panel: JPanel): IO[Unit] = IO { panel.setLayout(new BorderLayout()) }

  def panelComponentsAdded(panel: JPanel, components: JComponent*): IO[Unit] =
    IO { components.foreach(c => panel.add(c)) }

  def panelComponentAdded[A](panel: JPanel, component: JComponent, constraint: A = None): IO[Unit] =
    IO { panel.add(component, constraint) }

  // label-related
  def labelCreated(text: String): IO[JLabel] = IO { new JLabel(text) }

  def labelUpdated(label: JLabel, text: String): IO[Unit] = IO { label.setText(text) }

  // slider-related
  def sliderCreated(min: Int, max: Int, default: Int): IO[JSlider] = IO { new JSlider(min, max, default) }

  // button-related
  def buttonCreated(text: String): IO[JButton] = IO { new JButton(text) }

}
