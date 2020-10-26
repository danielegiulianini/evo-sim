package evo_sim.model

case class Environment(temperature: Int,
                       luminosity: Int,
                       initialBlobNumber: Int,
                       initialPlantsNumber: Int,
                       initialObstacleNumber: Int,
                       daysNumber: Int)