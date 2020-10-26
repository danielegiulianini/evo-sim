package evo_sim.model

import evo_sim.model.BoundingBox._
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Effect, Life, LifeCycle, MovementStrategy, Velocity}
import evo_sim.model.GenderEnum.GenderEnum

object GenderEnum extends Enumeration {
  type GenderEnum = Value
  val Male, Female, Genderless = Value
}

object EntityStructure {
  trait Domain {
    type Life
    type Velocity
    type DegradationEffect[A] >: A => Life
    type Effect //name to be changed
    type Position
    type MovementStrategy
    type Cooldown
    type LifeCycle
    type Gender
  }

  object DomainImpl extends Domain {
    override type Life = Int
    override type Velocity = Int
    override type DegradationEffect[A] = A => Life
    override type Effect = Blob => Set[SimulableEntity]  //name to be changed
    override type Position = Movement
    override type MovementStrategy = (Intelligent, World, Entity => Boolean) => Position
    override type Cooldown = Int
    override type LifeCycle = Int
    override type Gender = GenderEnum
  }

  trait Entity {
    def name : String
    def boundingBox: BoundingBox
    /*override def equals(obj: Any): Boolean = obj match {
      case _ : Entity => name.equals(obj.asInstanceOf[Entity].name)
      case _ => false
    }
    override def hashCode(): Int = name.hashCode()*/
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

  trait Sexed extends Entity {
    def gender: GenderEnum
  }

  trait Blob extends Entity with Living with Moving with Perceptive with Intelligent with Sexed {
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

  trait Plant extends Entity {
    def lifeCycle: LifeCycle
  }

  trait BlobWithTemporaryStatus extends Blob {
    def cooldown: Cooldown
  }
}
