package evo_sim.model.entities.entityStructure.movement

import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.{Entity, Intelligent}
import evo_sim.model.entities.entityStructure.Point2D
import evo_sim.model.world.{Constants, World}
import scala.math.{atan2, cos, hypot, sin, toDegrees, toRadians}

object DemonstrationCompletelyScalaMovement {

  private val random = new java.util.Random()

  def completelyScalaBaseMovement(entity: Intelligent, world: World, entitiesFilter: Entity => Boolean): Movement = {

    val chasedEntity = (world.entities - entity.asInstanceOf[SimulableEntity]).filter(entitiesFilter) match {
      case set if set.nonEmpty => Option(set.minBy(elem => distanceBetweenEntities(entity.boundingBox.point, elem.boundingBox.point)))
      case _ => None
    }

    chasedEntity match {
      case Some(chasedEntity) if (distanceBetweenEntities(entity.boundingBox.point, chasedEntity.boundingBox.point) < entity.fieldOfViewRadius) => chaseMovement(entity, chasedEntity.boundingBox.point)
      case _ => standardMovement(entity, entity.direction.angle, world)
    }

  }

  @scala.annotation.tailrec
  private def standardMovement(entity: Intelligent, angle: Int, world: World): Movement = {

    val direction = entity.direction.stepToNextDirection match {
      case Constants.DEF_NEXT_DIRECTION => Direction(random.nextInt(360), random.nextInt(50))
      case x => Direction(angle, x-1)
    }

    val positionUpdated = nextPosition(entity, direction.angle)

    isBoundaryCollision(positionUpdated, Point2D(world.width, world.height)) match {
      case true => standardMovement(entity, random.nextInt(360), world)
      case false => Movement(positionUpdated, direction)
    }
  }

  private def nextPosition(entity: Intelligent, angle: Int): Point2D = {
    val deltaX = entity.velocity * cos(toRadians(angle)) * 0.05
    val deltaY = entity.velocity * sin(toRadians(angle)) * 0.05
    val x = (entity.boundingBox.point.x + deltaX).toFloat.round
    val y = (entity.boundingBox.point.y + deltaY).toFloat.round
    Point2D(x, y)
  }

  private def chaseMovement(entity: Intelligent, chasedEntity: Point2D): Movement = {
    val angle = toDegrees(atan2(chasedEntity.y - entity.boundingBox.point.y, chasedEntity.x - entity.boundingBox.point.x)).toFloat.round
    Movement(nextPosition(entity, angle), Direction(angle, entity.direction.stepToNextDirection - 1))
  }

  private def isBoundaryCollision(entityPosition: Point2D, worldDimension: Point2D): Boolean = (entityPosition.x, entityPosition.y) match {
    case (x, y) if (0 until worldDimension.x contains x) && (0 until worldDimension.y contains y) => false
    case _ => true
  }

  private def distanceBetweenEntities(pointA: Point2D, pointB: Point2D): Double = {
    hypot(pointB.x - pointA.x, pointB.y - pointA.y)
  }

}
