package evo_sim.view.swing.monadic

import java.awt.{Dimension}

import cats.effect.IO
import javax.swing.JComponent
import javax.swing.border.Border


//class JComponentIO[T<:JComponent](jComponent: T) {
class JComponentIO[T<:JComponent](override val component: T) extends ContainerIO(component) {
  def minimumSizeSet(dimension: Dimension): IO[Unit] =
    IO {component.setMinimumSize(dimension)}
  def maximumSizeSet(dimension: Dimension): IO[Unit] =
    IO{ component.setMaximumSize(dimension)}
  def borderSet(border: Border): IO[Unit] = {
    IO {component.setBorder(border)}
  }
}

//companion object with utilities to be added
