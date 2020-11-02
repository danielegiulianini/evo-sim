package evo_sim.model.world

/** The Environment class defines a container for the initial properties for the simulations's world.
 *
 * @param temperature           the initial temperature of the world
 * @param luminosity            the initial luminosity of the world
 * @param initialBlobNumber     the initial blob number in the world
 * @param initialPlantNumber    the initial plant number in the world
 * @param initialObstacleNumber the initial obstacle number in the world
 * @param daysNumber            the total duration of the simulation in days
 */
case class Environment(temperature: Int,
                       luminosity: Int,
                       initialBlobNumber: Int,
                       initialPlantNumber: Int,
                       initialObstacleNumber: Int,
                       daysNumber: Int)
