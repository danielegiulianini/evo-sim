package evo_sim.model

import evo_sim.model.EntityBehaviour.{SimulableEntity}

case class World(currentIteration: Int, entities: Set[SimulableEntity])

//companion object
object World {
  def worldCreated(env: Environment): World = {
    val entities = ???  //blobSImulable implementation still missing
    World(currentIteration = 0, entities = entities)
  }
}



