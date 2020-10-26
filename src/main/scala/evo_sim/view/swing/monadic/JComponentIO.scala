package evo_sim.view.swing.monadic

import java.awt.Dimension

import cats.effect.IO
import javax.swing.JComponent
import evo_sim.view.swing.monadic.BorderFactoryIO
import javax.swing.border.Border


class JComponentIO[T<:JComponent](jComponent: T) {
  def minimumSizeSet(dimension: Dimension): IO[Unit] =
    IO {jComponent.setMinimumSize(dimension)}
  def maximumSizeSet(dimension: Dimension): IO[Unit] =
    IO{ jComponent.setMaximumSize(dimension)}
  def borderSet(border: Border): IO[Unit] =
    IO {jComponent.setBorder(border)}
}
