package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Intelligent

import scala.math._

object MovingStrategies {

  def baseMovement(entity: Intelligent, entities: Set[SimulableEntity]): Point2D = {
    val chasedEntity = (entities - entity.asInstanceOf[SimulableEntity]).minBy(distanceBetweenEntities(entity, _))
    if (distanceBetweenEntities(entity, chasedEntity) < entity.fieldOfViewRadius) chaseMovement(entity, chasedEntity)
    else standardMovement(entity)
  }

  def crazyMovement(entity: Intelligent, entities: Set[Intelligent]): Intelligent = ???

  private def distanceBetweenEntities(a: Intelligent, b: SimulableEntity): Double = {
    sqrt(pow(b.boundingBox.point.x - a.boundingBox.point.x, 2) + pow(b.boundingBox.point.y - a.boundingBox.point.y, 2))
  }

  @scala.annotation.tailrec
  private def standardMovement(entity: Intelligent): Point2D = {
    val angle = new java.util.Random().nextInt(360)
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    val x = entity.boundingBox.point.x + deltaX
    val y = entity.boundingBox.point.y + deltaY
    if (isBoundaryCollision(x, y)) standardMovement(entity) else Point2D(x.toInt, y.toInt)
  }

  @scala.annotation.tailrec
  private def chaseMovement(entity: Intelligent, chasedEntity: SimulableEntity): Point2D = {
    val angle = toDegrees(atan2(chasedEntity.boundingBox.point.y - entity.boundingBox.point.y, chasedEntity.boundingBox.point.x - entity.boundingBox.point.x))
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    val x = entity.boundingBox.point.x + deltaX
    val y = entity.boundingBox.point.y + deltaY
    if (isBoundaryCollision(x, y)) chaseMovement(entity, chasedEntity) else Point2D(x.toInt, y.toInt)
  }

  //DA MODIFICARE, bisogna considerare anche il raggio di grandezza del blob)
  private def isBoundaryCollision(x: Double, y: Double): Boolean = (x, y) match {
    //case (x, y) if x > World.width || y > World.height => true
    case (x, y) if x < 0 || y < 0 => true
    case _ => false
  }


}
