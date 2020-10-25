package evo_sim.view.swing.monadic

import java.awt.Dimension

import cats.effect.IO
import javax.swing.JComponent

class JComponentIO {
  class IOJComponent(jComponent: JComponent) {
    def setMinimumSize(dimension: Dimension): IO[Unit] =
      IO {jComponent.setMinimumSize(dimension)}
    def setMaximumSize(dimension: Dimension): IO[Unit] =
      IO{ jComponent.setMaximumSize(dimension)}
  }
}
