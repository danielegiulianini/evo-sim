package evo_sim.model

case class Environment(temperature: Int,
                       luminosity: Int,
                       initialBlobNumber: Int,
                       initialFoodNumber: Int,
                       initialObstacleNumber: Int,
                       daysNumber: Int)