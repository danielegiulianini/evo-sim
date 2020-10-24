package evo_sim.prolog

import java.io.FileInputStream

import alice.tuprolog._
import evo_sim.model.EntityStructure.Intelligent
import evo_sim.model.World

object MovingStrategiesProlog {

  def movement(entity: Intelligent, world: World):Unit = {
    val engine = new Prolog()

    val theory = new Theory(new FileInputStream("file.pl"))
    engine.setTheory(theory)

    var blobs: Term = new Struct()

    world.entities.foreach(elem => {
      val point: Term = new Struct("point", new Int(elem.boundingBox.point.x), new Int(elem.boundingBox.point.y))
      blobs = new Struct(point, blobs)
    })

    val nextPos = new Struct("nextPos", new Int(/*entity.direction.angle*/1), new Double(Math.PI), new Var("Y"))
    //0 to 10 foreach(elem => t = new Struct(new Struct("blob", new Struct("point", new Int(elem.bo)), new Int(0)), t))

    val goal: Term = new Struct("lista", blobs, new Var("X"))
    //val goal = nextPos


    val sol = engine.solve(goal)

    println(sol)
    println(sol.getSolution)
    while(engine.hasOpenAlternatives){
      val sol = engine.solveNext()
      println("\n"+sol)
      println(sol.getSolution)
    }
  }
}
