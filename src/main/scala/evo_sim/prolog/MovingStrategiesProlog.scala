package evo_sim.prolog

import alice.tuprolog._
import evo_sim.model.Constants.{ITERACTION_LAPSE, MAX_STEP_FOR_ONE_DIRECTION, WORLD_HEIGHT, WORLD_WIDTH}
import evo_sim.model.EntityStructure.Intelligent
import evo_sim.model.{Direction, Movement, Point2D}
import evo_sim.prolog.PrologEngine.engine

object MovingStrategiesProlog {

  implicit def termToInt(t:Term): scala.Int = t.toString.toInt

  implicit def toStructPoint(point: Point2D): Term = new Struct("point", new Int(point.x), new Int(point.y))

  implicit def toPrologInt(value: scala.Int): Int = new Int(value)

  implicit def toPrologDouble(value: scala.Double): Double = new Double(value)

  implicit def termToPoint2D(t: Term): Point2D = Point2D(extractVarValue(t,0), extractVarValue(t, 1))

  implicit def termToDirection(t:Term): Direction = Direction(extractVarValue(t, 0), extractVarValue(t, 1))

  def standardMovement(entity: Intelligent): Movement = {
    val pointVal = new Var("Point")
    val directionVal = new Var("Direction")
    val goal: Term = new Struct("standardMov", entity.boundingBox.point, entity.velocity,
                                  entity.direction.angle, entity.direction.stepToNextDirection, constantTerm,
                                  pointVal, directionVal)

    newPosition(goal, pointVal, directionVal)

  }

  def chaseMovement(entity: Intelligent, chasedEntity: Point2D): Movement = {
    val pointVal = new Var("Point")
    val directionVal = new Var("Direction")
    val goal: Term = new Struct("chaseMov", entity.boundingBox.point, chasedEntity, entity.velocity, constantTerm,
                                 pointVal, directionVal)

    newPosition(goal, pointVal, directionVal)
  }

  private def newPosition(goal: Term, pointVar: Var, directionVar: Var): Movement = {
    val solution = engine(goal)
    val solveInfo = solution.iterator.next()
    Movement(solveInfo.getVarValue(pointVar.getName), solveInfo.getVarValue(directionVar.getName))
  }

  private def extractVarValue(term: Term, argNumber: scala.Int): Term = term.asInstanceOf[Struct].getArg(argNumber)

  private def constantTerm: Term = new Struct("simulationConstants", scala.math.Pi, MAX_STEP_FOR_ONE_DIRECTION, WORLD_WIDTH, WORLD_HEIGHT, ITERACTION_LAPSE)

}
