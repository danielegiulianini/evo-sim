package evo_sim.model.entities.entityStructure.movement

import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.{Entity, Intelligent}
import evo_sim.model.entities.entityStructure.Point2D
import evo_sim.model.world.Constants.{DEF_NEXT_DIRECTION, ITERATION_LAPSE, MAX_DIRECTION_ANGLE, MAX_STEP_FOR_ONE_DIRECTION}
import evo_sim.model.world.{Constants, World}

import scala.math.{atan2, cos, hypot, sin, toDegrees, toRadians}

object DemonstrationCompletelyScalaMovement {

  private val random = new java.util.Random()

  /** Equivalent to [[MovingStrategies.baseMovement]] but fully implemented in Scala.
   *
   *  @param entity to be moved.
   *  @param world containing all the simulation information.
   *  @param entitiesFilter that describes all possible eatable entities.
   *  @return a Movement that contains the new position and the new direction.
   */
  def completelyScalaBaseMovement(entity: Intelligent, world: World, entitiesFilter: Entity => Boolean): Movement = {

    val chasedEntity = (world.entities - entity.asInstanceOf[SimulableEntity]).filter(entitiesFilter) match {
      case set if set.nonEmpty => Option(set.minBy(elem => distanceBetweenEntities(entity.boundingBox.point, elem.boundingBox.point)))
      case _ => None
    }

    chasedEntity match {
      case Some(chasedEntity) if distanceBetweenEntities(entity.boundingBox.point, chasedEntity.boundingBox.point) < entity.fieldOfViewRadius => chaseMovement(entity, chasedEntity.boundingBox.point)
      case _ => standardMovement(entity, entity.direction.angle, world)
    }

  }

  /** Calculates the new position based on the previous direction if 'Step' is different from 0, otherwise a new direction is chosen and the
   * new position will be calculated. It is also checked that the new position is inside the edges and if it is not the point is recalculated
   * by changing the direction.
   *
   * @param entity to be moved.
   * @param angle the entity direction.
   * @param world containing the simulation information.
   * @return a Movement that contains the new position and the new direction.
   */
  @scala.annotation.tailrec
  private def standardMovement(entity: Intelligent, angle: Int, world: World): Movement = {

    val direction = entity.direction.stepToNextDirection match {
      case DEF_NEXT_DIRECTION => Direction(random.nextInt(MAX_DIRECTION_ANGLE), random.nextInt(MAX_STEP_FOR_ONE_DIRECTION)+1)
      case x => Direction(angle, x-1)
    }

    val positionUpdated = nextPosition(entity, direction.angle)

    if (isBoundaryCollision(positionUpdated, Point2D(world.width, world.height)))
      standardMovement(entity, random.nextInt(MAX_DIRECTION_ANGLE), world)
    else
      Movement(positionUpdated, direction)

  }

  /** Calculates the new position based on direction, speed and time to perform an iteration.
   *
   * @param entity to be moved.
   * @param angle the entity direction.
   * @return the new position.
   */
  private def nextPosition(entity: Intelligent, angle: Int): Point2D = {
    val deltaX = entity.velocity * cos(toRadians(angle)) * ITERATION_LAPSE
    val deltaY = entity.velocity * sin(toRadians(angle)) * ITERATION_LAPSE
    val x = (entity.boundingBox.point.x + deltaX).toFloat.round
    val y = (entity.boundingBox.point.y + deltaY).toFloat.round
    Point2D(x, y)
  }

  /** Calculates the new position based on the previous position of the blob approaching the point passed as the second argument.
   *  StepToNextDirection is always set to 0, so when the entity stops chasing another entity it will change direction.
   *
   * @param entity to be moved.
   * @param chasedEntity the entity being chased
   * @return a Movement that contains the new position and the new direction.
   */
  private def chaseMovement(entity: Intelligent, chasedEntity: Point2D): Movement = {
    val angle = toDegrees(atan2(chasedEntity.y - entity.boundingBox.point.y, chasedEntity.x - entity.boundingBox.point.x)).toFloat.round
    Movement(nextPosition(entity, angle), Direction(angle, Constants.DEF_NEXT_DIRECTION))
  }

  /** Checks if the entity's location is within the world.
   *
   * @param entityPosition the 2-dimensional position of the entity.
   * @param worldDimension maximum size (width, height) of the world
   * @return false if the entity didn't hit any edges, true otherwise.
   */
  private def isBoundaryCollision(entityPosition: Point2D, worldDimension: Point2D): Boolean = (entityPosition.x, entityPosition.y) match {
    case (x, y) if (0 until worldDimension.x contains x) && (0 until worldDimension.y contains y) => false
    case _ => true
  }

  /**
   * Calculates the distance between two points on the 2-Dimensional Cartesian plane.
   * @param pointA the first point.
   * @param pointB the second point.
   * @return the distance between the two points passed as a parameter.
   */
  private def distanceBetweenEntities(pointA: Point2D, pointB: Point2D): Double = {
    hypot(pointB.x - pointA.x, pointB.y - pointA.y)
  }

}
