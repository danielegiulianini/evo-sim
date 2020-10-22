package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Collidable.NeutralCollidable
import evo_sim.model.Entities._
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus, Entity, Food, Obstacle}
import evo_sim.model.Updatable.{BaseBlobUpdatable, NeutralUpdatable}
import evo_sim.model.World._

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable

  //companion object with some simulable implementations ready to be (re)used (in the future)
  object Simulable {
    trait NeutralBehaviour extends NeutralCollidable with NeutralUpdatable {
      self : Entity =>
    }
  }

  //Base blob behaviour implementation
  trait BaseBlobBehaviour extends Simulable with BaseBlobUpdatable {
    self: BaseBlob =>
    override def collided(other: SimulableEntity): Set[SimulableEntity] = {
      other match {
        case food: Food => food.effect(self)
        case obstacle: Obstacle => obstacle.effect(self)
        case blob: CannibalBlob => if(blob.boundingBox.radius > self.boundingBox.radius) Set(self.copy(life = Constants.DEF_BLOB_DEAD)) else Set(self.copy())
        case _ => Set(self)
      }
    }
  }

  trait CannibalBlobBehaviour extends Simulable with BaseBlobUpdatable{
    self: CannibalBlob =>
    override def collided(other: SimulableEntity): Set[SimulableEntity] = {
      other match {
        case food: Food => food.effect(self)
        case obstacle: Obstacle => obstacle.effect(self)
        case base: BaseBlob => if(self.boundingBox.radius > base.boundingBox.radius) Set(self.copy(life=self.life+base.life)) else Set(self.copy())
        case cannibal: CannibalBlob => if(self.boundingBox.radius > cannibal.boundingBox.radius) Set(self.copy(life=self.life+cannibal.life)) else Set(self.copy(life = Constants.DEF_BLOB_DEAD))
        case _ => Set(self)
      }
    }
  }

  trait TempBlobBehaviour extends Simulable with NeutralCollidable {
    self: BlobWithTemporaryStatus =>
    override def updated(world: World): Set[SimulableEntity] = {
      def newSelf = self match {
        case blob: PoisonBlob => poisonBehaviour(blob, world)
        case blob: SlowBlob => slowBehaviour(blob, world)
        case _ => self
      }
      Set(newSelf)
    }
  }

  trait BaseFoodBehaviour extends Simulable {
    self: Food =>

    override def updated(world: World): Set[SimulableEntity] = {
      val life = self.degradationEffect(self)
      life match {
        case n if n > 0 => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, life, self.effect))
        case _ => Set(BaseFood(self.name, BoundingBox.Triangle(randomPosition(), self.boundingBox.height),
          self.degradationEffect, Constants.DEF_FOOD_LIFE, self.effect))
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, 0, self.effect))
      case _ => Set(self)
    }
  }


  private def poisonBehaviour(self: PoisonBlob, world: World): SimulableEntity = {
    val movement = self.movementStrategy(self, world)
    self.cooldown match {
      case n if n > 1 =>
        self.copy(
          boundingBox = Circle(movement.point, self.boundingBox.radius),
          direction = movement.direction,
          velocity = self.velocity + TemperatureEffect.standardTemperatureEffect(world.currentIteration),
          life = self.degradationEffect(self),
          fieldOfViewRadius = self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
          cooldown=self.cooldown - 1
        )
      case _ => BaseBlob(
        self.name,
        Circle(movement.point, self.boundingBox.radius),
        self.degradationEffect(self),
        self.velocity + TemperatureEffect.standardTemperatureEffect(world.currentIteration),
        DegradationEffect.baseBlobDegradation,
        self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
        self.movementStrategy,
        movement.direction)
    }
  }

  private def slowBehaviour(self: SlowBlob, world: World): SimulableEntity = {
    val movement = self.movementStrategy(self, world)
    self.cooldown match {
      case n if n > 1 =>
        self.copy(
          boundingBox = Circle(movement.point, self.boundingBox.radius),
          direction = movement.direction,
          velocity = Constants.DEF_BLOB_SLOW_VELOCITY,
          life = self.degradationEffect(self),
          fieldOfViewRadius = self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
          cooldown=self.cooldown - 1
        )
      case _ =>
        BaseBlob(
          self.name,
          Circle(movement.point, self.boundingBox.radius),
          self.degradationEffect(self),
          self.initialVelocity,
          DegradationEffect.baseBlobDegradation,
          self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
          self.movementStrategy,
          movement.direction)
    }
  }



}
