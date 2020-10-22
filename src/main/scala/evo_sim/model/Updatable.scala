package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob}
import evo_sim.model.EntityBehaviour.{Simulable, SimulableEntity}
import evo_sim.model.EntityStructure.{Blob, Entity}

trait Updatable {
  def updated(world: World) : Set[SimulableEntity]
}

//companion object with some updatable implementations ready to be (re)used (in the future)
object Updatable {

  trait NeutralUpdatable extends Simulable {
    self : Entity with Collidable =>
    override def updated(world:World): Set[SimulableEntity] = Set(self)
  }

  trait BaseBlobUpdatable extends Simulable {
    self: Blob with Collidable =>
    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world)
      self.life match {
        case n if n > 0 => self match {
          case _: BaseBlob => Set(BaseBlob(self.name, Circle(movement.point, self.boundingBox.radius),
            self.degradationEffect(self), self.velocity + TemperatureEffect.standardTemperatureEffect(world.currentIteration),
            self.degradationEffect, self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
            self.movementStrategy, self.direction))
          case _: CannibalBlob => Set(CannibalBlob(self.name, Circle(movement.point, self.boundingBox.radius),
            self.degradationEffect(self), self.velocity + TemperatureEffect.standardTemperatureEffect(world.currentIteration),
            self.degradationEffect, self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration),
            self.movementStrategy, self.direction))
        }
        case _ => Set()
      }
    }
  }
}

/**
Set(self.copy(
          boundingBox = Circle(movement.point, self.boundingBox.radius),
          direction = movement.direction,
          velocity = velocity + TemperatureEffect.standardTemperatureEffect(world.currentIteration),
          life = self.degradationEffect(self),
          fieldOfViewRadius = self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.currentIteration)
        ))*/