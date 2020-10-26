package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob}
import evo_sim.model.EntityBehaviour.{Simulable, SimulableEntity}
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Living}

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
      self.life match {
        case n if n > 0 => self match {
          case _: BaseBlob => {
            val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food])
            Set(BaseBlob(self.name, Circle(movement.point, self.boundingBox.radius),
              self.degradationEffect(self), self.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature),
              self.degradationEffect, self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity),
              self.gender, self.movementStrategy, movement.direction))
          }
          case _: CannibalBlob => {
            val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food] || e.isInstanceOf[BaseBlob])
            Set(CannibalBlob(self.name, Circle(movement.point, self.boundingBox.radius),
              self.degradationEffect(self), self.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature),
              self.degradationEffect, self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity),
              self.gender, self.movementStrategy, movement.direction))
          }
        }
        case _ => Set()
      }
    }
  }
}