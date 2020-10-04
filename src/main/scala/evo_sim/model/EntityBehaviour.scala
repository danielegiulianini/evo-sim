package evo_sim.model

object EntityBehaviour {
  trait Simulable extends Updatable with Collidable with Structured //type Simulable = Updatable with Collidable & Structured

  //stub for blob
  class SimulableBlob(baseBlob : BaseBlob) extends Simulable {//does nothing
    override def updated(world: World): Set[Simulable] = {print("baseBlob");Set(this)}
    override def collided(other: Simulable): Set[Simulable] = other match {
      case blob: SimulableBlob => Set(this)
      case _ =>Set(this)
    }
    override def structure: Entity = baseBlob
  }

  //other entities go here
/*
  //stub for food
  case class SimulableFood(baseFood : BaseFood) extends Simulable {
    override def updated(world: World): Set[Simulable] = {print("baseFood"); Set(this)}
    override def collided(other: Entity): Set[Simulable] = {Set(this)}
  }

  //stub for obstacle
  case class SimulableObstacle(baseObstacle: BaseObstacle) extends Simulable {
    override def updated(world: World): Set[Simulable] = {print("baseObstacle"); Set(this)}
    override def collided(other: Entity): Set[Simulable] = {Set(this)}
  }
*/
}
