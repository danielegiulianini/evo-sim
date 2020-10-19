package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities._
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Obstacle}
import evo_sim.model.World._

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable

  //Base blob behaviour implementation
  trait BaseBlobBehaviour extends Simulable {
    self: BaseBlob =>

    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world)
      self.life match {
        case n if n > 0 => Set(self.copy(
          boundingBox = Circle(movement.point, self.boundingBox.radius),
          direction = movement.direction,
          velocity = TemperatureEffect.standardTemperatureEffect(self, world.temperature),
          /*movementDirection = movement.angle,
          stepToNextDirection = movement.stepToNextDirection,*/
          life = self.degradationEffect(self),
          fieldOfViewRadius = self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration)
        ))
        case _ => Set()
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = {
      other match {
        case food: Food => food.effect(self)
        case obstacle: Obstacle => obstacle.effect(self)
        case _ => Set(self)
      }
    }
  }

  trait TempBlobBehaviour extends Simulable {
    self: TempBlob =>
    override def updated(world: World): Set[SimulableEntity] = {
      def newSelf = self match {
        case blob: PoisonBlob => poisonBehaviour(blob, world)
        case blob: SlowBlob => slowBehaviour(blob, world)
        case _ => self
      }
      Set(newSelf)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case food: Food => Set(self) //food.effect(self.blob)
      case obstacle: Obstacle => Set(self) //obstacle.effect(self.blob)
      case _ => Set(self)
    }

  }

  trait BaseFoodBehaviour extends Simulable {
    self: Food =>

    override def updated(world: World): Set[SimulableEntity] = {
      val life = self.degradationEffect(self)
      life match {
        case n if n > 0 => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, life, self.effect))
        case _ => Set(BaseFood(self.name, BoundingBox.Triangle(randomPosition(), Constants.DEF_FOOD_HEIGHT),
          self.degradationEffect, Constants.DEF_FOOD_LIFE, self.effect))
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, 0, self.effect))
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
      case n if n > 1 =>
        self.blob match {
          case base: BaseBlob => PoisonBlob(
            name = base.name,
            base.copy(
              boundingBox = Circle(movement.point, base.boundingBox.radius),
              direction = movement.direction,
              velocity = TemperatureEffect.standardTemperatureEffect(self.blob, world.temperature),
              /*movementDirection = movement.angle,
              stepToNextDirection = movement.stepToNextDirection,*/
              life = base.degradationEffect(base),
              fieldOfViewRadius = base.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration)),
            base.boundingBox,
            self.cooldown - 1)
          //TODO case _ => // other blobs
        }
      case _ =>
        self.blob match {
          case base: BaseBlob => base.copy(
            name = base.name,
            boundingBox = Circle(movement.point, base.boundingBox.radius),
            direction = movement.direction,
            velocity = TemperatureEffect.standardTemperatureEffect(self.blob, world.temperature),
            /*movementDirection = movement.angle,
            stepToNextDirection = movement.stepToNextDirection,*/
            life = base.degradationEffect(base),
            fieldOfViewRadius = base.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration)
          )
          //TODO case _ => // other blobs
        }
    }
  }

  private def slowBehaviour(self: SlowBlob, world: World): SimulableEntity = {
    val movement = self.blob.movementStrategy(self.blob, world)
    self.cooldown match {
      case n if n > 1 =>
        self.blob match {
          case base: BaseBlob => SlowBlob(
            name = base.name,
            base.copy(
              boundingBox = Circle(movement.point, base.boundingBox.radius),
              direction = movement.direction,
              velocity =  Constants.DEF_BLOB_SLOW_VELOCITY,
              /*movementDirection = movement.angle,
              stepToNextDirection = movement.stepToNextDirection,*/
              life = base.degradationEffect(base),
              fieldOfViewRadius = base.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration)),
            base.boundingBox,
            self.cooldown - 1,
            self.initialVelocity)
          //TODO case _ => // other blobs
        }
      case _ =>
        self.blob match {
          case base: BaseBlob => base.copy(
            boundingBox = Circle(movement.point, base.boundingBox.radius),
            direction = movement.direction,
            /*movementDirection = movement.angle,
            stepToNextDirection = movement.stepToNextDirection,*/
            life = base.degradationEffect(base),
            fieldOfViewRadius = base.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
            velocity = TemperatureEffect.standardTemperatureEffect(self.blob, world.temperature))
          //TODO case _ => // other blobs
        }
    }
  }

}
