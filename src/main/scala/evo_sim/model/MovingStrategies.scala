package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Intelligent

import scala.math._

case class Movement(point: Point2D, angle: Int, stepToNextDirection: Int)

object MovingStrategies {

  def baseMovement(entity: Intelligent, world: World): Movement = {

    val chasedEntity = world.entities - entity.asInstanceOf[SimulableEntity] match {
      case set if set.nonEmpty => Option(set.minBy(distanceBetweenEntities(entity, _)))
      case _ => None
    }

    chasedEntity match {
      //case Some(chasedEntity) if distanceBetweenEntities(entity, chasedEntity) < entity.fieldOfViewRadius => chaseMovement(entity, chasedEntity)
      case _ => standardMovement(entity, entity.movementDirection, world)
    }

  }

  def crazyMovement(entity: Intelligent, entities: Set[Intelligent]): Intelligent = ???

  private def distanceBetweenEntities(a: Intelligent, b: SimulableEntity): Double = {
    sqrt(pow(b.boundingBox.point.x - a.boundingBox.point.x, 2) + pow(b.boundingBox.point.y - a.boundingBox.point.y, 2))
  }

  //@scala.annotation.tailrec
  private def standardMovement(entity: Intelligent, angle: Int, world: World): Movement = {
    val rnd = new java.util.Random()
    val step = entity.stepToNextDirection
    val newAngle = step match {
      //case 0 => new java.util.Random().nextInt(360)
      case x if x < 0 => rnd.nextInt(360)
      //case _ => entity.movementDirection
      case _ => angle
    }
    /*if(t == 0) {
      angle = new java.util.Random().nextInt(360)
      t = new java.util.Random().nextInt(50) + 1
    } else
      t = t - 1*/

    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle)) * 0.05
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle)) * 0.05
    val x = (entity.boundingBox.point.x + deltaX).toFloat.round
    val y = (entity.boundingBox.point.y + deltaY).toFloat.round

    isBoundaryCollision(Point2D(x, y), Point2D(world.width, world.height)) match {
      case true => standardMovement(entity, new java.util.Random().nextInt(360), world)
      case false => Movement(Point2D(x, y), newAngle, if (step > 0) step - 1 else rnd.nextInt(50))
    }
    //Movement(Point2D(x, y), angle)
  }

  /*@scala.annotation.tailrec
  private def chaseMovement(entity: Intelligent, chasedEntity: SimulableEntity): Point2D = {
    val angle = toDegrees(atan2(chasedEntity.boundingBox.point.y - entity.boundingBox.point.y, chasedEntity.boundingBox.point.x - entity.boundingBox.point.x))
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    val x = entity.boundingBox.point.x + deltaX
    val y = entity.boundingBox.point.y + deltaY
    if (isBoundaryCollision(x, y)) chaseMovement(entity, chasedEntity) else Point2D(x.toInt, y.toInt)
  }*/

  //TODO: Bisogna considerare anche il raggio di grandezza del blob
  private def isBoundaryCollision(position: Point2D, worldDimension: Point2D): Boolean = (position.x, position.y) match {
    case (x, y) if (0 until worldDimension.x contains x) && (0 until worldDimension.y contains y) => false
    case _ => true
  }


}
