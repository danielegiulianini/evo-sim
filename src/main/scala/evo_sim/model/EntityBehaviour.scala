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
      Set(self.copy(
        boundingBox = Circle(self.movementStrategy(self, world.entities), self.boundingBox.radius),
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
        case n if n > 0 => Set(BaseFood(self.boundingBox, self.degradationEffect, life, self.effect))
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

  private def poisonBehaviour(self: PoisonBlob, world: World): SimulableEntity = self.cooldown match {
    case n if n > 1 => PoisonBlob(BaseBlob(Circle(self.blob.movementStrategy(self.blob, world.entities), self.blob.boundingBox.radius), self.blob.degradationEffect(self.blob), self.blob.velocity, self.blob.degradationEffect,
      self.blob.fieldOfViewRadius, self.blob.movementStrategy), self.boundingBox, self.cooldown - 1)
    case _ => BaseBlob(Circle(self.blob.movementStrategy(self.blob, world.entities), self.blob.boundingBox.radius), self.blob.degradationEffect(self.blob), self.blob.velocity, self.blob.degradationEffect,
      self.blob.fieldOfViewRadius, self.blob.movementStrategy)
  }

  private def slowBehaviour(self: SlowBlob, world: World): SimulableEntity = self.cooldown match {
    case n if n > 1 => SlowBlob(BaseBlob(Circle(self.blob.movementStrategy(self.blob, world.entities), self.blob.boundingBox.radius), self.blob.degradationEffect(self.blob), self.blob.velocity, self.blob.degradationEffect,
      self.blob.fieldOfViewRadius, self.blob.movementStrategy), self.boundingBox, self.cooldown - 1, self.initialVelocity)
    case _ => BaseBlob(Circle(self.blob.movementStrategy(self.blob, world.entities), self.blob.boundingBox.radius), self.blob.degradationEffect(self.blob), self.initialVelocity, self.blob.degradationEffect,
      self.blob.fieldOfViewRadius, self.blob.movementStrategy)
  }

  /*private def temporaryStatusBehaviour(self: BlobWithTemporaryStatus, world: World): SimulableEntity = self.cooldown match {
    case n if n > 1 => self match {
      case slowBlob: SlowBlob =>  SlowBlob(BaseBlob(Circle(slowBlob.blob.movementStrategy(slowBlob.blob, world.entities), self.blob.boundingBox.radius), slowBlob.blob.degradationEffect(slowBlob.blob), slowBlob.blob.velocity, slowBlob.blob.degradationEffect,
        slowBlob.blob.fieldOfViewRadius, slowBlob.blob.movementStrategy), slowBlob.boundingBox, slowBlob.cooldown - 1, slowBlob.initialVelocity)
      case poisonBlob: PoisonBlob => PoisonBlob(BaseBlob(Circle(poisonBlob.blob.movementStrategy(poisonBlob.blob, world.entities), poisonBlob.blob.boundingBox.radius), poisonBlob.blob.degradationEffect(poisonBlob.blob), poisonBlob.blob.velocity, poisonBlob.blob.degradationEffect,
        poisonBlob.blob.fieldOfViewRadius, poisonBlob.blob.movementStrategy), poisonBlob.boundingBox, poisonBlob.cooldown - 1)
    }
    case _ => BaseBlob(Circle(self.blob.movementStrategy(self.blob, world.entities), self.blob.boundingBox.radius), self.blob.degradationEffect(self.blob), self.initialVelocity, self.blob.degradationEffect,
      self.blob.fieldOfViewRadius, self.blob.movementStrategy)
  }*/

}
