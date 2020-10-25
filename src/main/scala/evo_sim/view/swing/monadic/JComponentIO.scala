package evo_sim.view.swing.monadic

import java.awt.Dimension

import cats.effect.IO
import javax.swing.JComponent

class IOJComponent(jComponent: JComponent) {
  def minimumSizeSet(dimension: Dimension): IO[Unit] =
    IO {jComponent.setMinimumSize(dimension)}
  def maximumSizeSet(dimension: Dimension): IO[Unit] =
    IO{ jComponent.setMaximumSize(dimension)}
}

