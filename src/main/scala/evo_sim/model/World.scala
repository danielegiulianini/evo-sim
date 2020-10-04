package evo_sim.model

import evo_sim.model.EntityBehaviour.Simulable

case class World(currentIteration: Int, entities: Set[_<:Simulable])

//companion object
object World {
  def worldCreated(env: Environment): World = {
    val entities = ???  //blobSImulable implementation still missing
    World(currentIteration = 0, entities = entities)
  }
}



