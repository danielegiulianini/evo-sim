package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World.DayPhase
import evo_sim.model.World.DayPhase.DayPhase

case class World(temperature: Int,
                 luminosity: Int,
                 width: Int, //to move in environment?
                 height: Int, //to move in environment?
                 currentIteration: Int,
                 entities: Set[SimulableEntity],
                 totalIterations: Int, //to move in environment?
                )

//companion object
object World {

  def apply(env: Environment): World = {
    val iterationsPerDay = 100
    val worldWidth = 1280
    val worldHeight = 720

    def randomPosition() = Point2D(new scala.util.Random().nextInt(worldWidth + 1), new scala.util.Random().nextInt(worldHeight + 1))
    val blobs: Set[SimulableEntity] = Iterator.tabulate(env.initialBlobNumber)(i => BaseBlob(
      name = "blob" + i,
      boundingBox = BoundingBox.Circle.apply(point = randomPosition(), radius = 5),
      life = Integer.MAX_VALUE,
      velocity = 50,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = 10,
      movementStrategy = MovingStrategies.baseMovement,
      movementDirection = 0,
      stepToNextDirection = 20)).toSet


    val foods: Set[SimulableEntity] = Iterator.tabulate(env.initialFoodNumber)(i => BaseFood(
      name = "food" + i,
      boundingBox = BoundingBox.Triangle.apply(point = randomPosition(), height = 10),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Integer.MAX_VALUE,
      effect = Effect.standardFoodEffect)).toSet

    val obstacles: Set[SimulableEntity] = Iterator.tabulate(env.initialObstacleNumber)(i => BaseObstacle(
      name = "obstacle" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = 15, height = 12),
      effect = Effect.neutralEffect)).toSet

    val entities = blobs ++ foods ++ obstacles

    World(temperature = env.temperature, luminosity = env.luminosity, width = worldWidth, height = worldHeight,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * iterationsPerDay)
  }

  def worldEnvironmentUpdated(world:World) = {

    // TODO: iterationsPerDay solo una volta nel codice (c'è anche in world)
    val iterationsPerDay: Int = 100
    val phaseDuration: Int = iterationsPerDay / DayPhase.values.size

    def asDayPhase(iteration: Int): DayPhase = iteration % iterationsPerDay match {
      case i if phaseDuration >= i => DayPhase.Night
      case i if phaseDuration + 1 to phaseDuration * 2 contains i => DayPhase.Morning
      case i if phaseDuration * 2 + 1 to phaseDuration * 3 contains i => DayPhase.Afternoon
      case i if phaseDuration * 3 < i => DayPhase.Evening
    }

    val currentDayPhase = asDayPhase(world.currentIteration)
    val nextDayPhase = asDayPhase(world.currentIteration + 1)

    case class EnvironmentModifiers(temperature: Int, luminosity: Int)

    def environmentModifiers: EnvironmentModifiers = (currentDayPhase != nextDayPhase, nextDayPhase) match {
      case (true, DayPhase.Night) => EnvironmentModifiers(-7, -15)
      case (true, DayPhase.Morning) => EnvironmentModifiers(+10, +25)
      case (true, DayPhase.Afternoon) => EnvironmentModifiers(+7, +15)
      case (true, DayPhase.Night) => EnvironmentModifiers(-10, -25)
      case _ => EnvironmentModifiers(0, 0)
    }

    environmentModifiers  //could return luminosity and temp updated instead of delta?
  }

  object DayPhase extends Enumeration {
    type DayPhase = Value
    val Morning, Afternoon, Evening, Night = Value
  }

}



