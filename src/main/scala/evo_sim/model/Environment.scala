package evo_sim.model

case class Environment(temperature: Int,
                       luminosity: Int,
                       initialBlobNumber: Int,
                       initialPlantNumber: Int,
                       initialObstacleNumber: Int,
                       daysNumber: Int)