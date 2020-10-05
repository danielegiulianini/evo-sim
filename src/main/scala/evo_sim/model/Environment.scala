package evo_sim.model

class Environment(temperature: Int,
                  luminosity: Int,
                  initialBlobNumber: Int,
                  initialFoodNumber: Int,
                  initialObstacleNumber: Int) {

  def Temperature: Int = temperature

  def Luminosity: Int = luminosity

  def InitialBlobNumber: Int = initialBlobNumber

  def InitialFoodNumber: Int = initialFoodNumber

  def InitialObstacleNumber: Int = initialObstacleNumber

}
