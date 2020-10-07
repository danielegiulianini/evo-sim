package evo_sim.model

import evo_sim.model.EntityBehaviour.{SimulableEntity}

case class World(width: Int, height: Int, currentIteration: Int, entities: Set[SimulableEntity])

//companion object
object World {
  def worldCreated(env: Environment): World = {
    val entities = ???  //blobSImulable implementation still missing
    World(width = 100, height = 100, currentIteration = 0, entities = entities)
  }
}



