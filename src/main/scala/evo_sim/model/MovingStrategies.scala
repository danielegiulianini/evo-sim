package evo_sim.model

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
    sqrt(pow(b.boundingBox.point._1 - a.boundingBox.point._1, 2) + pow(b.boundingBox.point._2 - a.boundingBox.point._2, 2))
  }

  private def standardMovement(entity: Intelligent): (Double, Double) = {
    println("standardMovement")
    val angle = new java.util.Random().nextInt(360)
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    (entity.boundingBox.point._1 + deltaX, entity.boundingBox.point._2 + deltaY)
  }

  private def chaseMovement(entity: Intelligent, chasedEntity: Entity): (Double, Double) = {
    println("chaseMovement")
    val angle = toDegrees(atan2(chasedEntity.boundingBox.point._2 - entity.boundingBox.point._2, chasedEntity.boundingBox.point._1 - entity.boundingBox.point._1))
    val deltaX = /*dt * */ entity.velocity * cos(toRadians(angle))
    val deltaY = /*dt * */ entity.velocity * sin(toRadians(angle))
    (entity.boundingBox.point._1 + deltaX, entity.boundingBox.point._2 + deltaY)
  }

  def crazyMovement(entity: Intelligent, entities: Set[Intelligent]): Intelligent = ???

}
