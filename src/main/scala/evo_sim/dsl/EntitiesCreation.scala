package evo_sim.dsl

import scala.language.postfixOps
import scala.language.higherKinds
import scala.language.reflectiveCalls
import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityStructure.{Entity, Intelligent}
import evo_sim.model.{DegradationEffect, Direction, EntityStructure, Movement, MovingStrategies, Point2D, World}


object Try extends App{
  import EntitiesCreation._
  val b = 5 of (create a BaseBlob
    where BaseBlobCreator.name is "name"
    and BaseBlobCreator.boundingBox is Circle(Point2D(0,0), 0)
    and BaseBlobCreator.life is 0
    and BaseBlobCreator.velocity is 0
    and BaseBlobCreator.fieldOfViewRadius is 0
    and BaseBlobCreator.direction is Direction(0, 0)
    and spawn it)
  //val c: CannibalBlob = create a CannibalBlob where "name" is "n2" and "boundingBox" is null and "life" is 0 and "velocity" is 0 and "degradationEffect" is null and "fieldOfViewRadius" is 0 and "movementStrategy" is null and "direction" is null and spawn it
  println(b.size)
}

object EntitiesCreation {
  /*This method enables syntax like this:

val baseBlobs = 15 of BaseBlob(
... blob properties...
)

    instead of:

val baseBlobs = Iterator.tabulate(15)(i => BaseBlob(
... blob properties...
)
  )*/

  implicit class FromIntToList(int:Int) {
    def of[T](t: =>T) = Iterator.fill(int)(t).toSet
  }

  object create {
    def a(t: BaseBlob.type) = new EntitySelection(BaseBlobCreator.baseBlob)
    def a(t: CannibalBlob.type) = new EntitySelection(cannibalBlob)
  }

  class EntitySelection[T](model: T) {
    def where(field: String): ValueDSL[EntityCreationProperty[T], Any] = new ValueDSL(value => {
      val f = model.getClass.getDeclaredField(field)
      f.setAccessible(true)
      /*if(f.getType.equals(value.getClass)){
        f.set(model, value)
      } else {
        throw new Exception(field + " is expected as " + f.getType.getClass.toString + " but found as " + value.getClass.toString)
      }*/
      f.set(model, value)
      new EntityCreationProperty[T](model)
    })

    def and(t: spawn.type): Object {
      def it: T
    } = new { def it: T = model }
  }

  class EntityCreationProperty[T](model: T) {
    def and(field: String): ValueDSL[EntityCreationProperty[T], Any] = new EntitySelection(model).where(field)

    def and(t: spawn.type): Object {
      def it: T
    } = new { def it: T = model }
  }

  object spawn

  class ValueDSL[T, V](callback: V => T) {
    def is(value: V): T = callback(value)
  }

  object standards{
    val standardDegradation = (A: EntityStructure.Living) => DegradationEffect.standardDegradation(A)
    val baseMovement = (entity: Intelligent, world: World, entitiesFilter: Entity => Boolean) => MovingStrategies.baseMovement(entity, world, entitiesFilter)
  }

  object BaseBlobCreator{
    val baseBlob: BaseBlob = BaseBlob("n", Circle(Point2D(0,0), 0), 0, 0, standards.standardDegradation, 0, standards.baseMovement, Direction(0, 0))
    val fields: List[String] = classOf[BaseBlob].getDeclaredFields.map(_.getName).toList
    def name: String = fields.head
    def boundingBox: String = fields(1)
    def life: String = fields(2)
    def velocity: String = fields(3)
    def fieldOfViewRadius: String = fields(5)
    def direction: String= fields(7)
  }
    val cannibalBlob: CannibalBlob = CannibalBlob(null, null, 0, 0, null, 0, null, null)
    val poisonBlob: PoisonBlob = PoisonBlob(null, null, 0, 0, null, 0, null, null, 0)
    val slowBlob: SlowBlob = SlowBlob(null, null, 0, 0, null, 0, null, null, 0, 0)


}