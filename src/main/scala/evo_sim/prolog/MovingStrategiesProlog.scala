package evo_sim.prolog

import alice.tuprolog._
import evo_sim.model.EntityStructure.Intelligent
import evo_sim.model.{Direction, Movement, Point2D}
import evo_sim.prolog.PrologEngine.engine

object MovingStrategiesProlog {

  implicit def termToInt(t:Term): scala.Int = t.toString.toInt

  implicit def toStructPoint(point: Point2D): Term = new Struct("point", new Int(point.x), new Int(point.y))

  implicit def toPrologInt(value: scala.Int): Int = new Int(value)

  implicit def toPrologDouble(value: scala.Double): Double = new Double(value)

  /*def chasedEntity(entity: Intelligent, entitiesSet: Set[SimulableEntity]):Option[Point2D] = {

    var entities: Term = new Struct()

    entitiesSet.foreach(elem => {
      entities = new Struct(new Struct("point", new Int(elem.boundingBox.point.x), new Int(elem.boundingBox.point.y)), entities)
    })

    val chasedVar = new Var("X")
    val blob: Term = new Struct("point", new Int(entity.boundingBox.point.x), new Int(entity.boundingBox.point.y))
    val goal: Term = new Struct("chasedMovement", blob, new Int(entity.fieldOfViewRadius), entities, chasedVar)

    val solution = engine(goal)

    if(solution.isEmpty){
      None
    } else {
      val chasedEntity = solution.iterator.next()
      Some(Point2D(extractVarValue(chasedEntity, chasedVar.getName, 0), extractVarValue(chasedEntity, chasedVar.getName, 1)))
    }

  }*/

  def standardMovement(entity: Intelligent): Movement = {
    val goal: Term = new Struct("standardMov", entity.boundingBox.point, entity.velocity,
                                  entity.direction.angle, entity.direction.stepToNextDirection,
                                  scala.math.Pi, new Var("Point"), new Var("Direction"))
    println("StandardMovement")
    println(entity)

    val solution = engine(goal)
    val nextPosition = solution.iterator.next()

    Movement(Point2D(extractVarValue(nextPosition,"Point", 0),extractVarValue(nextPosition,"Point", 1)),
              Direction(extractVarValue(nextPosition,"Direction", 0),extractVarValue(nextPosition, "Direction", 1)))

  }

  def chaseMovement(entity: Intelligent, chasedEntity: Point2D): Movement = {
    val goal: Term = new Struct("chaseMov", entity.boundingBox.point, chasedEntity, entity.velocity,
                                scala.math.Pi, new Var("Point"), new Var("Direction"))

    println("ChaseMovement")
    println(entity)
    println(chasedEntity)
    println(goal)
    val solution = engine(goal)
    val nextPosition = solution.iterator.next()

    Movement(Point2D(extractVarValue(nextPosition,"Point", 0),extractVarValue(nextPosition,"Point", 1)),
      Direction(extractVarValue(nextPosition,"Direction", 0),extractVarValue(nextPosition, "Direction", 1)))
  }

  private def extractVarValue(solveInfo: SolveInfo, varName: String, argNumber: scala.Int): Term =
    solveInfo.getVarValue(varName).asInstanceOf[Struct].getArg(argNumber)

}
