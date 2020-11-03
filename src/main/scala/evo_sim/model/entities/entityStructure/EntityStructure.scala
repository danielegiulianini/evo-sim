package evo_sim.model.entities.entityStructure

import scala.language.higherKinds
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.entities.entityStructure.EntityStructure.DomainImpl.{CollisionEffect, Cooldown, DegradationEffect, Life, LifeCycle, MovementStrategy, Velocity}
import evo_sim.model.entities.entityStructure.movement.{Direction, Movement}
import evo_sim.model.world.World

object EntityStructure {
  trait Domain {
    type Life
    type Velocity
    type DegradationEffect[A] >: A => Life
    type CollisionEffect
    type Position
    type MovementStrategy
    type Cooldown
    type LifeCycle
  }

  object DomainImpl extends Domain {
    override type Life = Int
    override type Velocity = Int
    override type DegradationEffect[A] = A => Life
    override type CollisionEffect = Blob => Set[SimulableEntity]
    override type Position = Movement
    override type MovementStrategy = (Intelligent, World, Entity => Boolean) => Position
    override type Cooldown = Int
    override type LifeCycle = Int
  }

  /**
   * This trait represent a standard entity in the simulation.
   */
  trait Entity {
    def name : String
    def boundingBox: BoundingBox
  }

  /**
   * This trait represent an entity in the simulation that can live or die.
   * [[evo_sim.model.entities.entityStructure.EntityStructure.Food]] and [[evo_sim.model.entities.entityStructure.EntityStructure.Blob]] eventually extends this trait.
   */
  sealed trait Living extends Entity {
    def life: Life
  }

  /**
   * This trait represent an entity in the simulation that can move in the world boundaries.
   * [[evo_sim.model.entities.entityStructure.EntityStructure.Blob]] eventually extends this trait.
   */
  sealed trait Moving extends Entity {
    def velocity: Velocity
  }

  /**
   * This trait represent an entity in the simulation that has some effects applied to a [[evo_sim.model.entities.entityStructure.EntityStructure.Blob]].
   * [[evo_sim.model.entities.entityStructure.EntityStructure.Food]] and [[evo_sim.model.entities.entityStructure.EntityStructure.Obstacle]] eventually extends this trait.
   */
  sealed trait Effectful extends Entity {
    def collisionEffect: CollisionEffect
  }

  /**
   * This trait represent an entity in the simulation that has the ability to see within a certain range.
   * [[evo_sim.model.entities.entityStructure.EntityStructure.Blob]] eventually extends this trait.
   */
  sealed trait Perceptive extends Entity {
    def fieldOfViewRadius : Int
  }

  /**
   * This trait represent an entity in the simulation that has the ability move in different directions following move strategies.
   * [[evo_sim.model.entities.entityStructure.EntityStructure.Blob]] eventually extends this trait.
   */
  sealed trait Intelligent extends Perceptive with Moving {
    def movementStrategy: MovementStrategy
    def direction: Direction
  }

  /**
   * This trait represent a blob abstraction entity.
   */
  trait Blob extends Entity with Living with Moving with Perceptive with Intelligent {
    override def boundingBox: Circle
    def degradationEffect: DegradationEffect[Blob]
  }

  /**
   * This trait represent a food abstraction entity.
   */
  trait Food extends Entity with Living with Effectful {
    override def boundingBox: Triangle
    def degradationEffect: DegradationEffect[Food]
  }

  /**
   * This trait represent a obstacle abstraction entity.
   */
  trait Obstacle extends Entity with Effectful {
    override def boundingBox: Rectangle
  }

  /**
   * This trait represent a plant abstraction entity.
   */
  trait Plant extends Entity {
    override def boundingBox: Rectangle
    def lifeCycle: LifeCycle
  }

  /**
   * This trait represent a temporary blob abstraction entity.
   */
  trait BlobWithTemporaryStatus extends Blob {
    def cooldown: Cooldown
  }
}
