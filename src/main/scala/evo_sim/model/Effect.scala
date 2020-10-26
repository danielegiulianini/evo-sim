package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.Velocity
import evo_sim.model.Utils._

object Effect {

  // adds 10 to blob life
  def standardFoodEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case b: BaseBlob => Set(b.copy(life = blob.life + Constants.DEF_FOOD_ENERGY))
    case b: CannibalBlob => Set(b.copy(life = blob.life + Constants.DEF_FOOD_ENERGY))
  }

  def reproduceBlobFoodEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life + Constants.DEF_FOOD_ENERGY), createChild(base))
    case cannibal: CannibalBlob => Set(cannibal.copy(life = cannibal.life + Constants.DEF_FOOD_ENERGY), createChild(cannibal))
    case poison: PoisonBlob => Set(poison.copy())
    case slow: SlowBlob => Set(slow.copy())
  }

  def poisonousFoodEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.as[PoisonBlob])
    case cannibal: CannibalBlob => Set(cannibal.as[CannibalBlob])
    case _ => Set()
  }

  // used for static entities
  def neutralEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy())
    case base: CannibalBlob => Set(base.copy())
    case poison: PoisonBlob => Set(poison.copy())
    case slow: SlowBlob => Set(slow.copy())
    case _ => Set()
  }

  def slowEffect(blob: Blob): Set[SimulableEntity] = {
    blob match {
      case base: BaseBlob => Set(base.as[SlowBlob])
      case cannibal: CannibalBlob => Set(cannibal.as[SlowBlob])
      case _ => Set()
    }
  }

  def damageEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case base: CannibalBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case _ => Set()
  }

  private def createChild(blob: Blob): SimulableEntity = blob match{
    case _: BaseBlob => BaseBlob(blob.name + "-son" + nextValue,
      Circle(blob.boundingBox.point, randomValueChange(Constants.DEF_BLOB_RADIUS, Constants.DEF_MOD_PROP_RANGE)),
      Constants.DEF_BLOB_LIFE, randomValueChange(blob.velocity, Constants.DEF_MOD_PROP_RANGE), blob.degradationEffect,
      randomValueChange(blob.fieldOfViewRadius, Constants.DEF_MOD_PROP_RANGE),
      MovingStrategies.baseMovement, Direction(blob.direction.angle, Constants.NEXT_DIRECTION))
    case _: CannibalBlob => CannibalBlob(blob.name + "-son" + nextValue,
      Circle(blob.boundingBox.point, randomValueChange(Constants.DEF_BLOB_RADIUS, Constants.DEF_MOD_PROP_RANGE)),
      Constants.DEF_BLOB_LIFE, randomValueChange(blob.velocity, Constants.DEF_MOD_PROP_RANGE), blob.degradationEffect,
      randomValueChange(blob.fieldOfViewRadius, Constants.DEF_MOD_PROP_RANGE),
      MovingStrategies.baseMovement, Direction(blob.direction.angle, Constants.NEXT_DIRECTION))
  }

}
