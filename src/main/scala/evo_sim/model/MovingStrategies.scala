package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.{Food, Intelligent}

import scala.math._

case class Movement(point: Point2D, direction: Direction)
case class Direction(angle: Int, stepToNextDirection: Int)

object MovingStrategies {
  private val random = new java.util.Random()

  def baseMovement(entity: Intelligent, world: World): Movement = {

    val chasedEntity = (world.entities - entity.asInstanceOf[SimulableEntity]).filter(elem => elem.isInstanceOf[Food]) match {
      case set if set.nonEmpty => Option(set.minBy(distanceBetweenEntities(entity, _)))
      case _ => None
    }

    chasedEntity match {
      case Some(chasedEntity) if (distanceBetweenEntities(entity, chasedEntity) < entity.fieldOfViewRadius) => {
        /*println("entity: " + entity.boundingBox)
        println("chased: " + chasedEntity.boundingBox)
        println("FOV: " + entity.fieldOfViewRadius + ", distance: " + distanceBetweenEntities(entity, chasedEntity))
        Thread.sleep(10000)*/
        chaseMovement(entity, chasedEntity)
      }
      case _ => standardMovement(entity, entity.direction.angle, world)
    }

  }

  //def crazyMovement(entity: Intelligent, entities: Set[Intelligent]): Intelligent = ???

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
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle)) * 0.05
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle)) * 0.05
    val x = (entity.boundingBox.point.x + deltaX).toFloat.round
    val y = (entity.boundingBox.point.y + deltaY).toFloat.round
    Point2D(x, y)
  }

  private def chaseMovement(entity: Intelligent, chasedEntity: SimulableEntity): Movement = {
    val angle = toDegrees(atan2(chasedEntity.boundingBox.point.y - entity.boundingBox.point.y, chasedEntity.boundingBox.point.x - entity.boundingBox.point.x)).toFloat.round
    Movement(nextPosition(entity, angle), Direction(angle, entity.direction.stepToNextDirection - 1))
  }

  //TODO: Bisogna considerare anche il raggio di grandezza del blob
  private def isBoundaryCollision(entityPosition: Point2D, worldDimension: Point2D): Boolean = (entityPosition.x, entityPosition.y) match {
    case (x, y) if (0 until worldDimension.x contains x) && (0 until worldDimension.y contains y) => false
    case _ => true
  }

  private def distanceBetweenEntities(a: Intelligent, b: SimulableEntity): Double = {
    sqrt(pow(b.boundingBox.point.x - a.boundingBox.point.x, 2) + pow(b.boundingBox.point.y - a.boundingBox.point.y, 2))
  }


}
