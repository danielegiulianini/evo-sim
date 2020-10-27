package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob

object BlobEntityHelper {
  protected[model] def fromTemporaryBlobToBaseBlob[A <: Blob](self: A, world: World, movement: Movement): SimulableEntity = {
    var velocity = self.velocity
    self match {
      case s:SlowBlob =>  velocity = s initialVelocity
      case _ => velocity = self.velocity
    }
    BaseBlob(self name, Circle(movement point, self.boundingBox.radius), self degradationEffect self, velocity,DegradationEffect baseBlobDegradation,
      self.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity), self movementStrategy, movement direction)
  }

  protected[model] def fromBlobToTemporaryBlob[A <: Blob](blob: A, blobType: String): SimulableEntity = blobType match {
    case Constants.POISONBLOB_TYPE => PoisonBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
      blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN)
    case Constants.SLOWBLOB_TYPE =>SlowBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
      blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN, blob velocity)
  }

  protected[model] def updateTemporaryBlob[A <: Blob](self: A, movement: Movement, world: World): SimulableEntity = self match {
    case base: BaseBlob => base.copy(
      boundingBox = base.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = base.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature),
      life = base.degradationEffect(base),
      fieldOfViewRadius = base.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity)
    )
    case cannibal: CannibalBlob => cannibal.copy(
      boundingBox = cannibal.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = cannibal.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature),
      life = cannibal.degradationEffect(cannibal),
      fieldOfViewRadius = cannibal.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity)
    )
    case slow: SlowBlob => slow.copy(
      boundingBox = slow.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = Constants.DEF_BLOB_SLOW_VELOCITY,
      life = slow.degradationEffect(slow),
      fieldOfViewRadius = slow.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity),
      cooldown = slow.cooldown - 1
    )
    case poison: PoisonBlob => poison.copy(
      boundingBox = poison.boundingBox.copy(point = movement.point),
      direction = movement.direction,
      velocity = poison.velocity + TemperatureEffect.standardTemperatureEffect(world.temperature),
      life = DegradationEffect.poisonBlobDegradation(poison),
      fieldOfViewRadius = poison.fieldOfViewRadius + LuminosityEffect.standardLuminosityEffect(world.luminosity),
      cooldown = poison.cooldown - 1
    )
  }

}
