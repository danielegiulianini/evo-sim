package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood}
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Obstacle}

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable


  //stub for blob (does nothing)
  trait BlobBehaviour extends Simulable {
    self: Blob =>

    override def updated(world: World): Set[SimulableEntity] = {
      print("baseBlob")
      Set(this)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: BaseBlob => Set(this)
      //case f: BaseFood => f.effect(self)
      //case o: BaseObstacle => o.effect(self)
    }
  }

  trait BaseFoodBehaviour extends Simulable {
    self: Food =>

    override def updated(world: World): Set[SimulableEntity] = {
      if (self.life - self.degradationEffect(self) <= 0) {
        Set()
      } else {
        Set(BaseFood(self.boundingBox, self.degradationEffect, self.life - self.degradationEffect(self), self.effect))
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
