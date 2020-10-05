package evo_sim.model

import evo_sim.model.BoundingBoxShape._
import evo_sim.model.DomainImpl.{DegradationEffect, Effect, Life, MovementStrategy, Velocity}
import evo_sim.model.EntityBehaviour.BlobBehaviour

trait Domain {
  type Life
  type Velocity
  type DegradationEffect >: Living => Life
  type Effect = Blob => Set[Entity]  //name to be changed
  type MovementStrategy = (Intelligent, Set[Entity]) => BoundingBoxShape
}

object DomainImpl extends Domain {
  override type Life = Int
  override type Velocity = Int
  override type DegradationEffect = Blob => Life
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
  def degradationEffect: DegradationEffect
}

trait Food extends Entity with Living with Effectful {
  override def boundingBox: Circle
}

trait Obstacle extends Entity with Effectful {
  override def boundingBox: Triangle
}


//----here ends data structure ----





//leaves of model hierarchy
case class BaseBlob(override val boundingBox: Rectangle,
                    override val life: Int,
                    override val velocity: Int,
                    override val degradationEffect: DegradationEffect,
                    override val fieldOfViewRadius: Int,
                    override val movementStrategy: MovementStrategy) extends Blob with BlobBehaviour

case class BaseFood(override val boundingBox: Circle,
                    override val life: Life,
                    override val effect: Effect) extends Food

case class BaseObstacle(override val boundingBox: Triangle,
                        override val effect: Effect) extends Obstacle





object Prova extends App {
  println("Ok")
}