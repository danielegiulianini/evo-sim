package evo_sim.view

import cats.effect.IO
import evo_sim.model.{Environment, World}

trait View {
  def inputReadFromUser(): IO[Environment]

  def rendered(world: World): IO[Unit]

  def resultViewBuiltAndShowed(world: World): IO[Unit]
}