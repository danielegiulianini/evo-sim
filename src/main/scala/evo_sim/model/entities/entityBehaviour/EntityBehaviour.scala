package evo_sim.model.entities.entityBehaviour

import evo_sim.model.entities.entityStructure.EntityStructure.DomainImpl.CollisionEffect
import evo_sim.model.entities.entityStructure.EntityStructure._
import evo_sim.model.entities.entityStructure.Point2D.randomPosition
import evo_sim.model.entities.Entities
import evo_sim.model.entities.Entities._
import evo_sim.model.entities.entityBehaviour.Collidable.NeutralCollidable
import evo_sim.model.entities.entityBehaviour.Updatable.NeutralUpdatable
import evo_sim.model.entities.entityStructure.effects.{CollisionEffect, DegradationEffect}
import evo_sim.model.entities.entityStructure.{BoundingBox, EntityStructure}
import evo_sim.model.world.{Constants, World}
import evo_sim.utils.Counter

object EntityBehaviour {

  /**
   * Defines the behavioural nature of a type implementing it, in contrast to the structural
   * nature of [[Entity]]ies.
   */
  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only

  /** Defines the type of the entities contained in [[World]] and, by doing so, it specifies the requirement
   * for a type to be part of the simulation, i.e. the need to be an [[Entity]] (structural component) and
   * to provide an [[Updatable]] and [[Collidable]] implementations (behavioural component)
   */
  type SimulableEntity = Entity with Simulable

  /** This companion object of [[Simulable]] provides some Simulable ready-to-be-(re)used self-type
   * implementations that can be stacked on an given [[Entity]] implementation, independently of the actual
   * Entity implementation using them.
   * By leveraging them, an [[Entity]] is ready to be part of the simulation.
   */
  object Simulable {
    /**
     * Defines a [[SimulableEntity]] implementation that does nothing on update and collide notifications,
     * returning on update and collision events the [[SimulableEntity]] as it was before,
     * without any modification, independently for the Entity on which that self-type is applied to.
     * Given an Entity, it's ready to be used for fulfilling the requirements needed to take part
     * in the simulation.
     */
    trait NeutralBehaviour extends NeutralCollidable with NeutralUpdatable {
      self: Entity =>
    }
  }

  //Base blob behaviour implementation
  /**
   * This Behaviour represent the base behaviour mainly for a Base [[EntityStructure.Blob]]. It contains two methods:
   * updated: check either the blob is dead or not. It returns a set containing the new blob with the updated values or an empty set if the blob is dead.
   * collided: when the blob collide with an entity this method is called. It can collide with different entities:
   * -[[EntityStructure.Food]]: apply the effect of the food at the blob.
   * -[[EntityStructure.Obstacle]]: apply the effect of the obstacle at the blob.
   * -[[Entities.CannibalBlob]]: if this blob is smaller than the cannibal, this blob is eaten.
   */
  trait BaseBlobBehaviour extends Simulable {
    self: BaseBlob =>
    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food])
      self.life match {
        case n if n > 0 => Set(BlobEntityHelper.updateBlob(self, movement, world))
        case _ => Set()
      }
    }
    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case food: Food => food.collisionEffect(self)
      case obstacle: Obstacle => obstacle.collisionEffect(self)
      case blob: CannibalBlob => if (blob.boundingBox.radius > self.boundingBox.radius)
        Set(self.copy(life = Constants.DEF_BLOB_DEAD)) else Set(self.copy())
      case _ => Set(self)
    }
  }

  /**
   * This Behaviour represent the base behaviour mainly for a Cannibal [[EntityStructure.Blob]].
   * Differently from a [[Entities.BaseBlob]] this blob moves towards a food or a base blob.
   * It contains two methods:
   * updated: check either the blob is dead or not. It returns a set containing the new blob with the updated values or an empty set if the blob is dead.
   * collided: when the blob collide with an entity this method is called. It can collide with different entities:
   * -[[EntityStructure.Food]]: apply the effect of the food at the blob.
   * -[[EntityStructure.Obstacle]]: apply the effect of the obstacle at the blob.
   * -[[Entities.BaseBlob]]: if this blob is bigger than the base blob, the base blob is eaten.
   */
  trait CannibalBlobBehaviour extends Simulable {
    self: CannibalBlob =>
    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food] || e.isInstanceOf[BaseBlob])
      self.life match {
        case n if n > 0 => Set(BlobEntityHelper.updateBlob(self, movement, world))
        case _ => Set()
      }
    }
    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case food: Food => food.collisionEffect(self)
      case obstacle: Obstacle => obstacle.collisionEffect(self)
      case base: BaseBlob => if (self.boundingBox.radius > base.boundingBox.radius) Set(self.copy(life = self.life + base.life)) else Set(self.copy())
      case _ => Set(self)
    }
  }

  /**
   * This Behaviour represent the base behaviour mainly for Temporary [[EntityStructure.Blob]].
   * updated: check either the blob is dead or not. It returns a set containing the new blob with the updated values or an empty set if the blob is dead.
   * This blob can't eat during the time the temporary effect is active. The effect remains until the cooldown is bigger than zero.
   * There are two effect applied:
   * [[Entities.PoisonBlob]]: this blob has a worst degradation effect, this mean that it takes more damage every time the degradation effect is applied.
   * [[Entities.SlowBlob]]: this blob moves significantly slowly.
   */
  trait TemporaryStatusBlobBehaviour extends Simulable with NeutralCollidable {
    self: BlobWithTemporaryStatus =>
    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food])
      self.life match {
        case n if n > 0 => self.cooldown match {
          case n if n > 0 => Set(BlobEntityHelper.updateBlob(self, movement, world))
          case _ => Set(BlobEntityHelper.fromTemporaryBlobToBaseBlob(self, world, movement))
        }
        case _ => Set()
      }
    }
  }

  /**
   * [[EntityStructure.Food]] behaviour. Makes a food disappear when life reaches 0 or collides with a [[EntityStructure.Blob]].
   */
  trait BaseFoodBehaviour extends Simulable {
    self: Food =>
    override def updated(world: World): Set[SimulableEntity] = {
      val life = self.degradationEffect(self)
      life match {
        case n if n > 0 => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, life, self.collisionEffect))
        case _ => Set()
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set()
      case _ => Set(self)
    }
  }

  /**
   * [[EntityStructure.Plant]] behaviour. Makes it spawn a [[EntityStructure.Food]] when lifeCycle reaches 0.
   */
  trait PlantBehaviour extends Simulable with NeutralCollidable {
    self: Plant with PlantBehaviour =>
    override def updated(world: World): Set[SimulableEntity] = self.lifeCycle match {
      case n if n > 0 =>
        Set(updatedPlant)
      case _ => Set(BaseFood(
        name = "generatedFood" + Counter.nextValue,
        boundingBox = BoundingBox.Triangle(point = randomPosition(), height = foodHeight),
        degradationEffect = DegradationEffect.standardDegradation,
        life = Constants.DEF_FOOD_LIFE,
        collisionEffect = foodEffect), defaultPlant)
    }

    def updatedPlant: Plant with PlantBehaviour
    def defaultPlant: Plant with PlantBehaviour
    def foodEffect: CollisionEffect
    def foodHeight: Int
  }

  /**
   * [[evo_sim.model.entities.entityBehaviour.EntityBehaviour.PlantBehaviour]] implementation for [[EntityStructure.Food]]s with [[CollisionEffect.standardFoodEffect]].
   */
  trait StandardPlantBehaviour extends PlantBehaviour {
    self: StandardPlant =>
    override def updatedPlant: StandardPlant = self.copy(lifeCycle = self.lifeCycle - 1)
    override def defaultPlant: StandardPlant = self.copy(lifeCycle = Constants.DEF_LIFECYCLE)
    override def foodEffect: Blob => Set[SimulableEntity] = CollisionEffect.standardFoodEffect
    override def foodHeight: Int = Constants.DEF_FOOD_HEIGHT
  }

  /**
   * [[evo_sim.model.entities.entityBehaviour.EntityBehaviour.PlantBehaviour]] implementation for [[EntityStructure.Food]]s with [[CollisionEffect.reproduceBlobFoodEffect]].
   */
  trait ReproducingPlantBehaviour extends PlantBehaviour {
    self: ReproducingPlant =>
    override def updatedPlant: ReproducingPlant = self.copy(lifeCycle = self.lifeCycle - 1)
    override def defaultPlant: ReproducingPlant = self.copy(lifeCycle = Constants.DEF_LIFECYCLE)
    override def foodEffect: Blob => Set[SimulableEntity] = CollisionEffect.reproduceBlobFoodEffect
    override def foodHeight: Int = Constants.DEF_REPRODUCING_FOOD_HEIGHT
  }

  /**
   * [[evo_sim.model.entities.entityBehaviour.EntityBehaviour.PlantBehaviour]] implementation for [[EntityStructure.Food]]s with [[CollisionEffect.poisonousFoodEffect]].
   */
  trait PoisonousPlantBehaviour extends PlantBehaviour {
    self: PoisonousPlant =>
    override def updatedPlant: PoisonousPlant = self.copy(lifeCycle = self.lifeCycle - 1)
    override def defaultPlant: PoisonousPlant = self.copy(lifeCycle = Constants.DEF_LIFECYCLE)
    override def foodEffect: Blob => Set[SimulableEntity] = CollisionEffect.poisonousFoodEffect
    override def foodHeight: Int = Constants.DEF_POISONOUS_FOOD_HEIGHT
  }
}
