package evo_sim.prolog

import scala.language.implicitConversions
import scala.language.higherKinds
import alice.tuprolog._
import evo_sim.model.Constants.{ITERATION_LAPSE, MAX_STEP_FOR_ONE_DIRECTION, WORLD_HEIGHT, WORLD_WIDTH}
import evo_sim.model.EntityStructure.Intelligent
import evo_sim.model.{Direction, Movement, Point2D}
import evo_sim.prolog.PrologEngine.engine

object MovingStrategiesProlog {

  /** Term containing the constant values needed to calculate the new position*/
  private val simulationConstantTerm: Term = new Struct("simulationConstants", scala.math.Pi, MAX_STEP_FOR_ONE_DIRECTION, WORLD_WIDTH, WORLD_HEIGHT, ITERATION_LAPSE)

  /** Implicit to convert a [[Term]] into a [[scala.Int]].
   *
   * @param t the term to convert.
   * @return the equivalent [[scala.Int]] value of the [[Term]].
   */
  implicit def termToInt(t:Term): scala.Int = t.toString.toInt

  /** Implicit to convert a [[scala.Int]] into a Prolog [[Int]].
   *
   * @param value the [[scala.Int]] to convert.
   * @return the Prolog [[Int]] equivalent at the [[scala.Int]].
   */
  implicit def toPrologInt(value: scala.Int): Int = new Int(value)

  /** Implicit to convert a [[scala.Double]] into a Prolog [[Double]].
   *
   * @param value the [[scala.Double]] to convert.
   * @return the Prolog [[Double]] equivalent at the [[scala.Double]].
   */
  implicit def toPrologDouble(value: scala.Double): Double = new Double(value)

  /** Implicit to convert a [[Point2D]] to a [[Term]].
   *
   * @param point the [[Point2D]] to convert.
   * @return the [[Term]] equivalent at the [[Point2D]].
   */
  implicit def toStructPoint(point: Point2D): Term = new Struct("point", new Int(point.x), new Int(point.y))

  /** Implicit to convert a [[Term]] to a [[Point2D]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[Point2D]] equivalent at the [[Term]].
   */
  implicit def termToPoint2D(t: Term): Point2D = Point2D(extractVarValue(t,0), extractVarValue(t, 1))

  /** Implicit to convert a [[Term]] to a [[Direction]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[Direction]] equivalent at the [[Term]].
   */
  implicit def termToDirection(t:Term): Direction = Direction(extractVarValue(t, 0), extractVarValue(t, 1))

  /** Calculates the new position following the previous direction if [[Direction.stepToNextDirection]] is different
   *  from 0, otherwise a new direction is chosen and the new position will be calculated.
   *
   * @param entity to be moved.
   * @return a [[Movement]] that contains the new position and the new direction.
   */
  def standardMovement(entity: Intelligent): Movement = {
    val pointVal = new Var("Point")
    val directionVal = new Var("Direction")
    val goal: Term = new Struct("standardMov", entity.boundingBox.point, entity.velocity,
      entity.direction.angle, entity.direction.stepToNextDirection, simulationConstantTerm,
      pointVal, directionVal)

    newPosition(goal, pointVal, directionVal)

  }

  /** Calculates the new position by chasing the nearest eatable entity. [[Direction.stepToNextDirection]] is always
   *  set to 0, so when the entity stops chasing another entity it will change direction.
   *
   * @param entity to be moved.
   * @param chasedEntity the entity to be chased
   * @return a [[Movement]] that contains the new position and the new direction.
   */
  def chaseMovement(entity: Intelligent, chasedEntity: Point2D): Movement = {
    val pointVal = new Var("Point")
    val directionVal = new Var("Direction")
    val goal: Term = new Struct("chaseMov", entity.boundingBox.point, chasedEntity, entity.velocity, simulationConstantTerm,
      pointVal, directionVal)

    newPosition(goal, pointVal, directionVal)
  }

  /** Create a [[Movement]] containing the new position and direction using the values contained in the
   * variables passed as parameters after solving the [[Term]] with the [[PrologEngine]].
   *
   * @param goal the term to solve.
   * @param pointVar the [[Var]] containing the information regarding the new [[Point2D]] position.
   * @param directionVar the [[Var]] containing the information regarding the new [[Direction]].
   * @return a [[Movement]] that contains the new position and the new direction.
   */
  private def newPosition(goal: Term, pointVar: Var, directionVar: Var): Movement = {
    val solution = engine(goal)
    val solveInfo = solution.iterator.next()
    Movement(solveInfo.getVarValue(pointVar.getName), solveInfo.getVarValue(directionVar.getName))
  }

  /** Gets the i-th [[Term]] contained in the [[Term]] passed as a parameter.
   *
   * @param term the term whence extract the information.
   * @param argNumber the index of the argument to extract.
   * @return the i-th [[Term]] extracted.
   */
  private def extractVarValue(term: Term, argNumber: scala.Int): Term = term.asInstanceOf[Struct].getArg(argNumber)

}
