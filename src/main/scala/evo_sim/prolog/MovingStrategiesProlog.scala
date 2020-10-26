package evo_sim.prolog

import java.io.FileInputStream

import alice.tuprolog._
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Intelligent
import evo_sim.model.{Direction, Movement, Point2D}

object MovingStrategiesProlog {

  val engine = new Prolog()

  val theory = new Theory(new FileInputStream("file.pl"))
  engine.setTheory(theory)

  implicit def termToInt(t:Term): scala.Int = t.toString.toInt

  def chasedEntity(entity: Intelligent, entitiesSet: Set[SimulableEntity]):Option[Point2D] = {

    var entities: Term = new Struct()

    entitiesSet.foreach(elem => {
      entities = new Struct(new Struct("point", new Int(elem.boundingBox.point.x), new Int(elem.boundingBox.point.y)), entities)
    })

    val blob: Term = new Struct("point", new Int(entity.boundingBox.point.x), new Int(entity.boundingBox.point.y))
    val goal: Term = new Struct("chasedMovement", blob, new Int(entity.fieldOfViewRadius), entities, new Var("X"))

    val sol = engine.solve(goal)

    if(sol.isSuccess){
      val x: scala.Int = sol.getTerm("X").asInstanceOf[Struct].getArg(0)
      val y: scala.Int = sol.getTerm("X").asInstanceOf[Struct].getArg(1)
      Some(Point2D(x, y))
    } else {
      None
    }

    /*while(engine.hasOpenAlternatives){
      val sol = engine.solveNext()
      println("\n"+sol)
      println(sol.getSolution)
    }*/
  }

  def standardMovement(entity: Intelligent): Movement = {
    val entityPoint: Term = new Struct("point", new Int(entity.boundingBox.point.x), new Int(entity.boundingBox.point.y))
    val goal: Term = new Struct("standardMov", entityPoint, new Int(entity.velocity),
                                  new Int(entity.direction.angle), new Int(entity.direction.stepToNextDirection),
                                  new Double(scala.math.Pi), new Var("Point"), new Var("Direction"))

    println(goal)

    val sol = engine.solve(goal)
    val x: scala.Int = sol.getTerm("Point").asInstanceOf[Struct].getArg(0)
    val y: scala.Int = sol.getTerm("Point").asInstanceOf[Struct].getArg(1)
    println(x)
    println(y)
    val angle: scala.Int = sol.getTerm("Direction").asInstanceOf[Struct].getArg(0)
    val step: scala.Int = sol.getTerm("Direction").asInstanceOf[Struct].getArg(1)
    println(angle)
    println(step)

    Movement(Point2D(x,y), Direction(angle,step))

  }
}
