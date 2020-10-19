package evo_sim.model

import evo_sim.model.BoundingBox._
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Effect, Life, MovementStrategy, Velocity}

object EntityStructure {
  trait Domain {
    type Life
    type Velocity
    type DegradationEffect[A] >: A => Life
    type Effect = Blob => Set[SimulableEntity]  //name to be changed
    type Position
    type MovementStrategy
    type Cooldown
  }

  object DomainImpl extends Domain {
    override type Life = Int
    override type Velocity = Int
    override type DegradationEffect[A] = A => Life
    override type Position = Movement
    override type MovementStrategy = (Intelligent, World) => Position
    override type Cooldown = Int
  }

  trait Entity {
    def name : String
    def boundingBox: BoundingBox
    override def equals(obj: Any): Boolean = obj match {
      case _ : Entity => name.equals(obj.asInstanceOf[Entity].name)
      case _ => false
    }
    override def hashCode(): Int = name.hashCode()
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
    def direction: Direction
  }

  trait Blob extends Entity with Living with Moving with Perceptive with Intelligent {
    override def boundingBox: Circle
    def degradationEffect: DegradationEffect[Blob]
  }

  trait Food extends Entity with Living with Effectful {
    override def boundingBox: Triangle
    def degradationEffect: DegradationEffect[Food]
  }

  trait Obstacle extends Entity with Effectful {
    override def boundingBox: Rectangle
  }

  trait BlobWithTemporaryStatus {
    def blob: Blob
    def cooldown: Cooldown
  }
}
