package evo_sim.model

object EntityBehaviour {
  trait Simulable extends Updatable with Collidable //type Simulable = Updatable with Collidable

  //stub for blob
  class SimulableBlob(baseBlob : BaseBlob) extends Simulable {
    override def updated(world: World): Set[Simulable] = {print("baseBlob"); Set(this)}
    override def collided(other: Simulable): Set[Simulable] = Set(this)
  }

  //other entities go here
}
