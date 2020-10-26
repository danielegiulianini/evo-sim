package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.{Entity, Intelligent}
import evo_sim.prolog.MovingStrategiesProlog

import scala.math._

case class Movement(point: Point2D, direction: Direction)
case class Direction(angle: Int, stepToNextDirection: Int)

object MovingStrategies {
  private val random = new java.util.Random()

  def baseMovement(entity: Intelligent, world: World, entitiesFilter: Entity => Boolean): Movement = {

    val chasedEntity = MovingStrategiesProlog.chasedEntity(entity, (world.entities - entity.asInstanceOf[SimulableEntity]).filter(entitiesFilter))

    chasedEntity match {
      case Some(chasedEntity) if (distanceBetweenEntities(entity.boundingBox.point, chasedEntity) < entity.fieldOfViewRadius) => chaseMovement(entity, chasedEntity)
      case _ => MovingStrategiesProlog.standardMovement(entity)
    }

    /*val chasedEntity = (world.entities - entity.asInstanceOf[SimulableEntity]).filter(entitiesFilter) match {
      case set if set.nonEmpty => Option(set.minBy(elem => distanceBetweenEntities(entity.boundingBox.point, elem.boundingBox.point)))
      case _ => None
    }

    chasedEntity match {
      case Some(chasedEntity) if (distanceBetweenEntities(entity.boundingBox.point, chasedEntity.boundingBox.point) < entity.fieldOfViewRadius) => chaseMovement(entity, chasedEntity.boundingBox.point)
      case _ => standardMovement(entity, entity.direction.angle, world)
    }*/

  }


  @scala.annotation.tailrec
  private def standardMovement(entity: Intelligent, angle: Int, world: World): Movement = {

    val direction = entity.direction.stepToNextDirection match {
      case Constants.NEXT_DIRECTION => Direction(random.nextInt(360), random.nextInt(50))
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

  private def distanceBetweenEntities(a: Point2D, b: Point2D): Double = {
    sqrt(pow(b.x - a.x, 2) + pow(b.y - a.y, 2))
  }

}
