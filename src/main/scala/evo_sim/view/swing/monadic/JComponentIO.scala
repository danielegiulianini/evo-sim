package evo_sim.view.swing.monadic

import java.awt.{Dimension}

import cats.effect.IO
import javax.swing.JComponent
import javax.swing.border.Border


/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JComponent]] in the form
 * of IO monad in a purely functional fashion.
 * Every Swing's JComponent could be wrapped by this class, but note that this package provided some ad-hoc factory
 * utilities for the most popular Swing's jComponents (see [[JPanelIO]], [[JFrameIO]], [[JButtonIO]]).
 * @param component the jComponent that this class wraps.
 * @tparam T the type of the component to be wrapped. and whose methods are to be enhanced with IO description.
 */class JComponentIO[T<:JComponent](override val component: T) extends ContainerIO(component) {
  def minimumSizeSet(dimension: Dimension): IO[Unit] =
    IO {component.setMinimumSize(dimension)}
  def maximumSizeSet(dimension: Dimension): IO[Unit] =
    IO{ component.setMaximumSize(dimension)}
  def borderSet(border: Border): IO[Unit] = {
    IO {component.setBorder(border)}
  }
}
