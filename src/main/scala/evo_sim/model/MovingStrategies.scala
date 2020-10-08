package evo_sim.model

import evo_sim.model.EntityStructure.{Entity, Intelligent}

import scala.math._

object MovingStrategies {

  def baseMovement(entity: Intelligent, entities: Set[Entity]): (Double, Double) = {
    val chasedEntity = (entities - entity).minBy(distanceBetweenEntities(entity, _))
    distanceBetweenEntities(entity, chasedEntity) < entity.fieldOfViewRadius match {
      case true => chaseMovement(entity, chasedEntity)
      case false => standardMovement(entity)
    }
  }

  private def distanceBetweenEntities(a: Intelligent, b: Entity): Double = {
    sqrt(pow(b.boundingBox.point.x - a.boundingBox.point.x, 2) + pow(b.boundingBox.point.y - a.boundingBox.point.y, 2))
  }

  private def standardMovement(entity: Intelligent): (Double, Double) = {
    println("standardMovement")
    val angle = new java.util.Random().nextInt(360)
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    (entity.boundingBox.point.x + deltaX, entity.boundingBox.point.y + deltaY)
  }

  private def chaseMovement(entity: Intelligent, chasedEntity: Entity): (Double, Double) = {
    println("chaseMovement")
    val angle = toDegrees(atan2(chasedEntity.boundingBox.point.y - entity.boundingBox.point.y, chasedEntity.boundingBox.point.x - entity.boundingBox.point.x))
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    (entity.boundingBox.point.x + deltaX, entity.boundingBox.point.y + deltaY)
  }

  def crazyMovement(entity: Intelligent, entities: Set[Intelligent]): Intelligent = ???

}
