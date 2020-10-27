package evo_sim.model

import evo_sim.model.Collidable.NeutralCollidable
import evo_sim.model.Entities._
import evo_sim.model.EntityStructure.DomainImpl.Effect
import evo_sim.model.EntityStructure._
import evo_sim.model.Updatable.NeutralUpdatable
import evo_sim.model.Utils._
import evo_sim.model.World._

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable

  //companion object with some simulable implementations ready to be (re)used (in the future)
  object Simulable {

    trait NeutralBehaviour extends NeutralCollidable with NeutralUpdatable {
      self: Entity =>
    }
  }

  //Base blob behaviour implementation
  trait BaseBlobBehaviour extends Simulable {
    self: BaseBlob =>
    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food])
      self.life match {
        case n if n > 0 => Set(BlobEntityHelper.updateTemporaryBlob(self, movement, world))
        case _ => Set()
      }
    }
    override def collided(other: SimulableEntity): Set[SimulableEntity] = {
      other match {
        case food: Food => food.effect(self)
        case obstacle: Obstacle => obstacle.effect(self)
        case blob: CannibalBlob => if (blob.boundingBox.radius > self.boundingBox.radius)
          Set(self.copy(life = Constants.DEF_BLOB_DEAD)) else Set(self.copy())
        case _ => Set(self)
      }
    }
  }

  trait CannibalBlobBehaviour extends Simulable {
    self: CannibalBlob =>
    override def updated(world: World): Set[SimulableEntity] = {
      val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food] || e.isInstanceOf[BaseBlob])
      self.life match {
        case n if n > 0 => Set(BlobEntityHelper.updateTemporaryBlob(self, movement, world))
        case _ => Set()
      }
    }
    override def collided(other: SimulableEntity): Set[SimulableEntity] = {
      other match {
        case food: Food => food.effect(self)
        case obstacle: Obstacle => obstacle.effect(self)
        case base: BaseBlob => if (self.boundingBox.radius > base.boundingBox.radius) Set(self.copy(life = self.life + base.life)) else Set(self.copy())
        case _ => Set(self)
      }
    }
  }

  trait TempBlobBehaviour extends Simulable with NeutralCollidable {
    self: BlobWithTemporaryStatus =>
    override def updated(world: World): Set[SimulableEntity] = self match {
        case blob: PoisonBlob => Set(poisonBehaviour(blob, world))
        case blob: SlowBlob => Set(slowBehaviour(blob, world))
        case _ => Set()
    }
  }

  /**
   * [[evo_sim.model.EntityStructure.Food]] behaviour. Makes a food disappear when life reaches 0 or collides with a [[evo_sim.model.EntityStructure.Blob]].
   */
  trait BaseFoodBehaviour extends Simulable {
    self: Food =>
    override def updated(world: World): Set[SimulableEntity] = {
      val life = self.degradationEffect(self)
      life match {
        case n if n > 0 => Set(BaseFood(self.name, self.boundingBox, self.degradationEffect, life, self.effect))
        case _ => Set()
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set()
      case _ => Set(self)
    }
  }

  /**
   * [[evo_sim.model.EntityStructure.Plant]] behaviour. Makes it spawn a [[evo_sim.model.EntityStructure.Food]] when lifeCycle reaches 0.
   */
  trait PlantBehaviour extends Simulable with NeutralCollidable {
    self: Plant with PlantBehaviour =>
    override def updated(world: World): Set[SimulableEntity] = {
      self.lifeCycle match {
        case n if n > 0 =>
          Set(updatedPlant)
        case _ => Set(BaseFood(
          name = "generatedFood" + nextValue,
          boundingBox = BoundingBox.Triangle(point = randomPosition(), height = foodHeight),
          degradationEffect = DegradationEffect.standardDegradation,
          life = Constants.DEF_FOOD_LIFE,
          effect = foodEffect), defaultPlant)
      }
    }

    def updatedPlant: Plant with PlantBehaviour
    def defaultPlant: Plant with PlantBehaviour
    def foodEffect: Effect
    def foodHeight: Int
  }

  /**
   * [[evo_sim.model.EntityBehaviour.PlantBehaviour]] implementation for [[evo_sim.model.EntityStructure.Food]]s with [[evo_sim.model.Effect.standardFoodEffect]].
   */
  trait StandardPlantBehaviour extends PlantBehaviour {
    self: StandardPlant =>
    override def updatedPlant = self.copy(lifeCycle = self.lifeCycle - 1)
    override def defaultPlant = self.copy(lifeCycle = Constants.DEF_LIFECYCLE)
    override def foodEffect = Effect.standardFoodEffect
    override def foodHeight = Constants.DEF_FOOD_HEIGHT
  }

  /**
   * [[evo_sim.model.EntityBehaviour.PlantBehaviour]] implementation for [[evo_sim.model.EntityStructure.Food]]s with [[evo_sim.model.Effect.reproduceBlobFoodEffect]].
   */
  trait ReproducingPlantBehaviour extends PlantBehaviour {
    self: ReproducingPlant =>
    override def updatedPlant = self.copy(lifeCycle = self.lifeCycle - 1)
    override def defaultPlant = self.copy(lifeCycle = Constants.DEF_LIFECYCLE)
    override def foodEffect = Effect.reproduceBlobFoodEffect
    override def foodHeight = Constants.DEF_REPRODUCING_FOOD_HEIGHT
  }

  /**
   * [[evo_sim.model.EntityBehaviour.PlantBehaviour]] implementation for [[evo_sim.model.EntityStructure.Food]]s with [[evo_sim.model.Effect.poisonousFoodEffect]].
   */
  trait PoisonousPlantBehaviour extends PlantBehaviour {
    self: PoisonousPlant =>
    override def updatedPlant = self.copy(lifeCycle = self.lifeCycle - 1)
    override def defaultPlant = self.copy(lifeCycle = Constants.DEF_LIFECYCLE)
    override def foodEffect = Effect.poisonousFoodEffect
    override def foodHeight = Constants.DEF_POISONOUS_FOOD_HEIGHT
  }

  private def poisonBehaviour(self: PoisonBlob, world: World) = {
    val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food])
    self.cooldown match {
      case n if n > 1 => BlobEntityHelper.updateTemporaryBlob(self, movement, world)
      case _ => BlobEntityHelper.fromTemporaryBlobToBaseBlob(self, world, movement)
    }
  }

  private def slowBehaviour(self: SlowBlob, world: World) = {
    val movement = self.movementStrategy(self, world, e => e.isInstanceOf[Food])
    self.cooldown match {
      case n if n > 1 => BlobEntityHelper.updateTemporaryBlob(self, movement, world)
      case _ => BlobEntityHelper.fromTemporaryBlobToBaseBlob(self, world, movement)
    }
  }
}
