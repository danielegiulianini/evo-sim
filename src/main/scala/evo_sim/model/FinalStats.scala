package evo_sim.model

import scala.language.implicitConversions
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.World.WorldHistory

object FinalStats {

  /** Implicit conversion from [[Int]][[Stream]] to [[Double]][[List]]
   *
   * @param stream the stream to convert.
   * @return the [[List]] equivalent at the [[Stream]]
   */
  implicit def intStreamToDoubleList(stream: Stream[Int]): List[Double] = stream.map(_.toDouble).toList

  /** Implicit conversion from [[Double]][[Stream]] to [[Double]][[List]]
   *
   * @param stream the stream to convert.
   * @return the [[List]] equivalent at the [[Stream]]
   */
  implicit def doubleStreamToDoubleList(stream: Stream[Double]): List[Double] = stream.toList

  /** Function that takes as input a function that determines which entity must be considered and returns
   *  a function that counts the number of entities present in the world considering only the entities selected
   *  by the function passed as parameter.
   */
  val entityDayQuantity: (SimulableEntity => Boolean) => World => Double = (valueConsidered: SimulableEntity => Boolean) => (world: World) => entityQuantity(world, valueConsidered)

  /** Function that takes as input a function that determines which genetic characteristic of the blob must be
   *  considered and returns a function that calculate the average of the genetic characteristic specified by
   *  the function passed as parameter for each Blob present in the simulation.
   */
  val entityCharacteristicAverage: (SimulableEntity => Int) => World => Double = (elemToChoose: SimulableEntity => Int) => (world: World) => blobGeneticCharacteristic(world, elemToChoose)

  /**
   * Creates a [[List]] containing the simulation day index.
   * @param totalIterations the number of iterations that the simulation has performed.
   * @return a [[List]] containing the simulation day index.
   */
  def days(totalIterations: Int): List[Double] = {
    (0 to totalIterations/Constants.ITERATIONS_PER_DAY).toList.map(_.toDouble)
  }

  /** Calculates the average value for the entire simulation of the field taken into
   *  consideration by the function passed as the second argument.
   *
   * @param history a [[WorldHistory]] containing the information of all the iterations performed by the simulation.
   * @param valueConsidered information taken into consideration.
   * @return the average value for the entire simulation.
   */
  def averageSimulation(history: WorldHistory)(valueConsidered: World => Double): Double = {
    val consideredDaysValue = dayValue(history)(valueConsidered)
    consideredDaysValue.sum / consideredDaysValue.length
  }

  /** Calculates the average value at the end of each day of the field taken into
   *  consideration by the function passed as the second argument at the end of each day.
   *
   * @param history a [[WorldHistory]] containing the information of all the iterations performed by the simulation.
   * @param valueConsidered information taken into consideration.
   * @return the average value for the entire simulation.
   */
  def dayValue(history: WorldHistory)(valueConsidered: World => Double): List[Double] = {
    val dayHistory = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayHistory.map(valueConsidered)
  }

  /** Calculates the average value considering the whole day of the field taken into
   *  consideration by the function passed as the second argument at the end of each day.
   *
   * @param history a [[WorldHistory]] containing the information of all the iterations performed by the simulation.
   * @param averageValueConsidered function that calculates the average of the characteristic taken into consideration
   * @return the average value for the entire simulation.
   */
  def averageDuringDay(history: WorldHistory)(averageValueConsidered: World => Double): List[Double] = {
    val origin = averageValueConsidered(history.head)
    val valueAverageDays = history.grouped(Constants.ITERATIONS_PER_DAY).map(_.map(singleIteration => averageValueConsidered(singleIteration)).sum / Constants.ITERATIONS_PER_DAY).toList
    origin :: valueAverageDays
  }

  /* private val blobGeneticCharacteristic: (World, SimulableEntity => Int) => Double = (world, getGeneticChar) => {
    val blobVelocity = world.entities.filter(_.isInstanceOf[Blob]).toList.map(getGeneticChar)
    blobVelocity.sum / blobVelocity.length
  } */
  /**
   * Function that calculate the average of the genetic characteristic specified by
   * the function passed as parameter for each Blob present in the simulation.
   *
   * @return the average of one day of the genetic characteristic considered
   */
  private def blobGeneticCharacteristic: (World, SimulableEntity => Int) => Double = (world, getGeneticChar) => {
    val blobVelocity = world.entities.filter(_.isInstanceOf[Blob]).toList.map(getGeneticChar)
    blobVelocity.sum / blobVelocity.length
  }

  //private val entityQuantity: (World, SimulableEntity => Boolean) => Double =  (world, elemToChoose) => world.entities.count(elemToChoose)
  /**
   * Function that counts the number of entities present in the world considering only the entities selected
   * by the function passed as parameter.
   *
   * @return the counts of entities considered in one day.
   */
  private def entityQuantity: (World, SimulableEntity => Boolean) => Double =  (world, elemToChoose) => world.entities.count(elemToChoose)

}
