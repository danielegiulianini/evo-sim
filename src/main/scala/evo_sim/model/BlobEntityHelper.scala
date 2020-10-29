package evo_sim.model

import scala.language.postfixOps
import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob

object BlobEntityHelper {
  /**
   * Takes a [[evo_sim.model.EntityStructure.Blob]] and transform it in a [[evo_sim.model.Entities.BaseBlob]] type, using the input blob's parameters.
   * @param self the initial blob.
   * @param world used to update some parameters.
   * @param movement used to update the move direction of the entity.
   * @tparam A accept [[evo_sim.model.EntityStructure.Blob]] subtype.
   * @return The new [[evo_sim.model.Entities.BaseBlob]] updated.
   */
  protected[model] def fromTemporaryBlobToBaseBlob[A <: Blob](self: A, world: World, movement: Movement): SimulableEntity = {
    var velocity = self.velocity
    self match {
      case s: SlowBlob => velocity = s initialVelocity
      case _ => velocity = self.velocity
    }
    BaseBlob(self name, Circle(movement point, self.boundingBox.radius), self degradationEffect self, velocity, DegradationEffect standardDegradation,
      self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity, world.currentIteration), self movementStrategy, movement direction)
  }

  /**
   * Takes a [[evo_sim.model.EntityStructure.Blob]] and transform it in a [[evo_sim.model.EntityStructure.BlobWithTemporaryStatus]] type
   * using the input blob's parameters.
   * @param blob the initial blob.
   * @param blobType specify the blob type to return.
   * @tparam A accept Blob subtype.
   * @return The new [[evo_sim.model.EntityStructure.BlobWithTemporaryStatus]] updated.
   */
  protected[model] def fromBlobToTemporaryBlob[A <: Blob](blob: A, blobType: String): SimulableEntity = blobType match {
    case Constants.POISONBLOB_TYPE => PoisonBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
      blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN)
    case _ /*Constants.SLOWBLOB_TYPE*/ => SlowBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
      blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN, blob velocity)
  }

  /**
   * This method is used to update a [[evo_sim.model.EntityStructure.Blob]].
   * This method can be used by the [[evo_sim.model.EntityStructure.Blob]] subtypes:
   * [[evo_sim.model.Entities.BaseBlob]]
   * [[evo_sim.model.Entities.PoisonBlob]]
   * [[evo_sim.model.Entities.CannibalBlob]]
   * [[evo_sim.model.Entities.SlowBlob]]
   * @param self the blob to be updated.
   * @param movement the movement direction updated.
   * @param world used to update some parameters.
   * @tparam A accept Blob subtype.
   * @return The new [[evo_sim.model.EntityBehaviour.SimulableEntity]] updated.
   */
  protected[model] def updateTemporaryBlob[A <: Blob](self: A, movement: Movement, world: World): SimulableEntity = self match {
    case base: BaseBlob => base.copy(
      boundingBox = base.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = base.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature, world.currentIteration),
      life = base.degradationEffect(base),
      fieldOfViewRadius = base.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity, world.currentIteration)
    )
    case cannibal: CannibalBlob => cannibal.copy(
      boundingBox = cannibal.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = cannibal.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature, world.currentIteration),
      life = cannibal.degradationEffect(cannibal),
      fieldOfViewRadius = cannibal.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity, world.currentIteration)
    )
    case slow: SlowBlob => slow.copy(
      boundingBox = slow.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = Constants.DEF_BLOB_SLOW_VELOCITY,
      life = slow.degradationEffect(slow),
      fieldOfViewRadius = slow.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity, world.currentIteration),
      cooldown = slow.cooldown - 1
    )
    case poison: PoisonBlob => poison.copy(
      boundingBox = poison.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = poison.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature, world.currentIteration),
      life = DegradationEffect.poisonBlobDegradation(poison),
      fieldOfViewRadius = poison.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity, world.currentIteration),
      cooldown = poison.cooldown - 1
    )
    case _ => throw new Exception("Sub type not supported.")
  }

}
