package evo_sim.model

import evo_sim.model.BoundingBox.Rectangle
import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle, SlowBlob}
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Obstacle}

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable


  //stub for blob (does nothing)
  trait BaseBlobBehaviour extends Simulable {
    self: Blob =>
    override def updated(world: World): Set[SimulableEntity] = {
      /* I blob con un trait che estende da BlobWithTemporaryStatus devono decrementare il cooldown dei loro status,
          se questo diviene 0 lo status corrispondente deve essere eliminato */
      def newSelf = self match {
        case s: SlowBlob if s.slownessCooldown > 1 => SlowBlob(s.boundingBox, s.life, s.velocity, s.degradationEffect,
          s.fieldOfViewRadius, s.movementStrategy, s.slownessCooldown - 1, s.initialVelocity)
        case s: SlowBlob => BaseBlob(s.boundingBox, s.life, s.velocity, s.degradationEffect,
          s.fieldOfViewRadius, s.movementStrategy)
        case _ => self
      }
      //ritorna bb self.movementStrategy(self, world.entities)
      //Set(new BaseBlob(Rectangle(self.movementStrategy(self, world.entities).point, self.boundingBox.width, self.boundingBox.height),
      //  self.degradationEffect,self.velocity, self,degradationEffect, self.fieldOfViewRadius, self.movementStrategy))
      Set(newSelf)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case blob: BaseBlob => Set(self)
      case food: BaseFood => food.effect(self)
      case obstacle: BaseObstacle => obstacle.effect(self)
      case _ => Set(this)
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
      case _ => Set(this)
    }
  }

  trait NeutralBehaviour extends Simulable {
    self: Obstacle =>

    override def updated(world: World): Set[SimulableEntity] = Set(this)

    override def collided(other: SimulableEntity): Set[SimulableEntity] = Set(this)
  }

}
