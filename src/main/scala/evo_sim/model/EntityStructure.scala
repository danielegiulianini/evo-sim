package evo_sim.model

import evo_sim.model.EntityStructure.DomainImpl.{DegradationEffect, Life, Velocity, Effect, MovementStrategy}
import evo_sim.model.BoundingBoxShape._


object EntityStructure {
  trait Domain {
    type Life
    type Velocity
    type DegradationEffect[A] >: A => Life
    type Effect = Blob => Set[Entity]  //name to be changed
    type Rivals = Set[Entity]
    type MovementStrategy = (Intelligent, Rivals) => BoundingBoxShape
  }

  object DomainImpl extends Domain {
    override type Life = Int
    override type Velocity = Int
    override type DegradationEffect[A] = A => Life
  }

  trait Entity {
    def boundingBox: BoundingBoxShape
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
    override def boundingBox: Rectangle
    def degradationEffect: DegradationEffect[Blob]
  }

  trait Food extends Entity with Living with Effectful {
    override def boundingBox: Circle
    def degradationEffect: DegradationEffect[Food]
  }

  trait Obstacle extends Entity with Effectful {
    override def boundingBox: Triangle
  }

}
