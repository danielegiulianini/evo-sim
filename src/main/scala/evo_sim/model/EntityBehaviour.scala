package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities._
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus, Entity, Food, Obstacle}

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable

  //Base blob behaviour implementation
  trait BaseBlobBehaviour extends Simulable {
    self: BaseBlob =>

    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world)
      Set(self.copy(
        boundingBox = Circle(movement.point, self.boundingBox.radius),
        movementDirection = movement.angle,
        stepToNextDirection = self.stepToNextDirection - 1,
        life = self.degradationEffect(self),
        fieldOfViewRadius = self.fieldOfViewRadius + world.luminosity
      ))
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = {
      other match {
        case _: Blob => Set(self)
        case food: BaseFood => food.effect(self)
        case obstacle: BaseObstacle => obstacle.effect(self)
        case _ => Set(self)
      }
    }
  }

  trait TempBlobBehaviour extends Simulable {
    self: TempBlob =>
    override def updated(world: World): Set[SimulableEntity] = {
      def newSelf = self.blob match {
        case blob: PoisonBlob => poisonBehaviour(blob, world)
        case blob: SlowBlob => slowBehaviour(blob, world)
        case _ => self
      }

      Set(newSelf)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set(self)
      case food: BaseFood => food.effect(self.blob)
      case obstacle: BaseObstacle => obstacle.effect(self.blob)
      case _ => Set(self)
    }

  }

  trait BaseFoodBehaviour extends Simulable {
    self: Food =>

    override def updated(world: World): Set[SimulableEntity] = {
      val life = self.degradationEffect(self)
      life match {
        case n if n > 0 => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, life, self.effect))
        case _ => Set()
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set()
      case _ => Set(self)
    }
  }

  trait NeutralBehaviour extends Simulable {
    self: Obstacle =>

    override def updated(world: World): Set[SimulableEntity] = Set(self)

    override def collided(other: SimulableEntity): Set[SimulableEntity] = Set(self)
  }

  private def poisonBehaviour(self: PoisonBlob, world: World): SimulableEntity = {
    val movement = self.blob.movementStrategy(self.blob, world)
    self.cooldown match {
      case n if n > 1 => {
        self.blob match {
          case base: BaseBlob => PoisonBlob(
            name = base.name,
            base.copy(
            boundingBox = Circle(movement.point, base.boundingBox.radius),
            movementDirection = movement.angle,
            stepToNextDirection = base.stepToNextDirection - 1,
            life = base.degradationEffect(base),
            fieldOfViewRadius = base.fieldOfViewRadius + world.luminosity),
            self.boundingBox,
            self.cooldown - 1)
          // case _ => // other blobs
        }
      }
      case _ => {
        self.blob match {
          case base: BaseBlob => base.copy(
            name = base.name,
            boundingBox = Circle(movement.point, base.boundingBox.radius),
            movementDirection = movement.angle,
            stepToNextDirection = base.stepToNextDirection - 1,
            life = base.degradationEffect(base),
            fieldOfViewRadius = base.fieldOfViewRadius + world.luminosity
          )
          // case _ => // other blobs
        }
      }
    }
  }

  private def slowBehaviour(self: SlowBlob, world: World): SimulableEntity = {
    val movement = self.blob.movementStrategy(self.blob, world)
    self.cooldown match {
      case n if n > 1 => {
        self.blob match {
          case base: BaseBlob => SlowBlob(
            name = base.name,
            base.copy(
            boundingBox = Circle(movement.point, base.boundingBox.radius),
            movementDirection = movement.angle,
            stepToNextDirection = base.stepToNextDirection - 1,
            life = base.degradationEffect(base),
            fieldOfViewRadius = base.fieldOfViewRadius + world.luminosity),
            self.boundingBox,
            self.cooldown - 1,
            self.initialVelocity)
          // case _ => // other blobs
        }
      }
      case _ => {
        self.blob match {
          case base: BaseBlob => base.copy(
            boundingBox = Circle(movement.point, base.boundingBox.radius),
            movementDirection = movement.angle,
            stepToNextDirection = base.stepToNextDirection - 1,
            life = base.degradationEffect(base),
            fieldOfViewRadius = base.fieldOfViewRadius + world.luminosity,
            velocity = self.initialVelocity)
            // case _ => // other blobs
        }
      }
    }
  }

}
