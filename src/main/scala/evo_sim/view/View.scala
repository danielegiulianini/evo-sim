package evo_sim.view

import evo_sim.model.{Environment, World}

trait View {
  def inputViewBuiltAndShowed(): Unit

  def inputReadFromUser(): Environment

  def simulationViewBuiltAndShowed(): Unit

  def rendered(world: World): Unit

  def resultViewBuiltAndShowed(world: World): Unit
}