package evo_sim.model.entities.entityBehaviour

import evo_sim.model.entities.entityStructure.BoundingBox.Circle
import evo_sim.model.entities.entityStructure.EntityStructure.Blob
import evo_sim.model.world.effects.{LuminosityEffect, TemperatureEffect}
import evo_sim.model.entities.Entities
import evo_sim.model.entities.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure
import evo_sim.model.entities.entityStructure.effects.DegradationEffect
import evo_sim.model.entities.entityStructure.movement.Movement
import evo_sim.model.world.{Constants, World}

object BlobEntityHelper {
  /**
   * Takes a [[EntityStructure.Blob]] and transform it in a [[Entities.BaseBlob]] type, using the input blob's parameters.
   * @param self the initial blob.
   * @param world used to update some parameters.
   * @param movement used to update the move direction of the entity.
   * @tparam A accept [[EntityStructure.Blob]] subtype.
   * @return The new [[Entities.BaseBlob]] updated.
   */
  protected[model] def fromTemporaryBlobToBaseBlob[A <: Blob](self: A, world: World, movement: Movement): SimulableEntity = {
    var velocity = self.velocity
    self match {
      case s: SlowBlob => velocity = s initialVelocity
      case _ => velocity = self.velocity
    }
    BaseBlob(self name, Circle(movement point, self.boundingBox.radius), self degradationEffect self, velocity, DegradationEffect standardDegradation,
      (self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity, world.currentIteration)).max(Constants.MIN_BLOB_FOV_RADIUS),
      self movementStrategy, movement direction)
  }

  /**
   * Takes a [[EntityStructure.Blob]] and transform it in a [[EntityStructure.BlobWithTemporaryStatus]] type
   * using the input blob's parameters.
   * @param blob the initial blob.
   * @param blobType specify the blob type to return.
   * @tparam A accept Blob subtype.
   * @return The new [[EntityStructure.BlobWithTemporaryStatus]] updated.
   */
  protected[model] def fromBlobToTemporaryBlob[A <: Blob](blob: A, blobType: String): SimulableEntity = blobType match {
    case Constants.POISONBLOB_TYPE => PoisonBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
      blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN)
    case _ /*Constants.SLOWBLOB_TYPE*/ => SlowBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
      blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN, blob velocity)
  }

  /**
   * This method is used to update a [[EntityStructure.Blob]].
   * This method can be used by the [[EntityStructure.Blob]] subtypes:
   * [[Entities.BaseBlob]]
   * [[Entities.PoisonBlob]]
   * [[Entities.CannibalBlob]]
   * [[Entities.SlowBlob]]
   * @param self the blob to be updated.
   * @param movement the movement direction updated.
   * @param world used to update some parameters.
   * @tparam A accept Blob subtype.
   * @return The new [[EntityBehaviour.SimulableEntity]] updated.
   */
  protected[model] def updateBlob[A <: Blob](self: A, movement: Movement, world: World): SimulableEntity = self match {
    case base: BaseBlob => base.copy(
      boundingBox = base.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = incrementedValue(base.velocity, TemperatureEffect.standardTemperatureEffect, world, Constants.MIN_BLOB_VELOCITY),
      life = base.degradationEffect(base),
      fieldOfViewRadius = incrementedValue(base.fieldOfViewRadius, LuminosityEffect.standardLuminosityEffect, world, Constants.MIN_BLOB_FOV_RADIUS)
    )
    case cannibal: CannibalBlob => cannibal.copy(
      boundingBox = cannibal.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = incrementedValue(cannibal.velocity, TemperatureEffect.standardTemperatureEffect, world, Constants.MIN_BLOB_VELOCITY),
      life = cannibal.degradationEffect(cannibal),
      fieldOfViewRadius = incrementedValue(cannibal.fieldOfViewRadius, LuminosityEffect.standardLuminosityEffect, world, Constants.MIN_BLOB_FOV_RADIUS)
    )
    case slow: SlowBlob => slow.copy(
      boundingBox = slow.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = Constants.DEF_BLOB_SLOW_VELOCITY,
      life = slow.degradationEffect(slow),
      fieldOfViewRadius = incrementedValue(slow.fieldOfViewRadius, LuminosityEffect.standardLuminosityEffect, world, Constants.MIN_BLOB_FOV_RADIUS),
      cooldown = slow.cooldown - 1
    )
    case poison: PoisonBlob => poison.copy(
      boundingBox = poison.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = incrementedValue(poison.velocity, TemperatureEffect.standardTemperatureEffect, world, Constants.MIN_BLOB_VELOCITY),
      life = DegradationEffect.poisonBlobDegradation(poison),
      fieldOfViewRadius = incrementedValue(poison.fieldOfViewRadius, LuminosityEffect.standardLuminosityEffect, world, Constants.MIN_BLOB_FOV_RADIUS),
      cooldown = poison.cooldown - 1
    )
    case _ => throw new Exception("Sub type not supported.")
  }

  private def incrementedValue(value: Int, effect: ((Int, Int)) => Int, world: World, min: Int): Int = {
    (value + effect(world.luminosity, world.currentIteration)).max(min)
  }

}
