package evo_sim.model

import evo_sim.model.EntityStructure.{Blob, Food}
import evo_sim.model.World.WorldHistory

object FinalStats {

  implicit def listToDoubleArray(list: List[Int]): Array[Double] = list.map(_.toDouble).toArray

  def population(history: WorldHistory): Array[Double] = {
    val dayInformation = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayInformation.map(_.entities.count(_.isInstanceOf[Blob])).toList
  }

  def food(history: WorldHistory): Array[Double] = {
    val dayInformation = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayInformation.map(_.entities.count(_.isInstanceOf[Food])).toList
  }

}
