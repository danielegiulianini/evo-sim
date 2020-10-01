package evo_sim.model

import java.awt.geom.RectangularShape

import evo_sim.model.DomainImpl.{DegradationEffect, Effect, Life, MovementStrategy, Velocity}

trait Domain {
  type Life
  type Velocity
  type DegradationEffect >: Living => Life
  type Effect = Blob => Set[Entity]  //name to be changed
  type MovementStrategy = (Intelligent, Set[Entity]) => BoundingBox
}

object DomainImpl extends Domain {
  override type Life = Int
  override type Velocity = Int
  override type DegradationEffect = Blob => Life
}

trait Entity {
  def boundingBox: BoundingBox
}

trait Living extends Entity {
  def life: Life
}

trait Moving extends Entity {
  def velocity: Velocity
}

trait Effectful extends Entity {
  def effect: Effect
}

trait Perceptive extends Entity {
  def fieldOfViewRadius : Int
}

trait Intelligent extends Perceptive with Moving {
  def movementStrategy: MovementStrategy
}

trait Blob extends Entity with Living with Moving with Perceptive with Intelligent {
  override def boundingBox: RectangularBoundingBox
  def degradationEffect: DegradationEffect
}


object Prova extends App {
  println("Ok")
}