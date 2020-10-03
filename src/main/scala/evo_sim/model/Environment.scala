package evo_sim.model

class Environment(temperature: Integer,
                  luminosity: Integer,
                  initialBlobsNumber: Integer,
                  initialFoodNumber: Integer,
                  initialObstacleNumber: Integer) {

  def Temperature: Integer = temperature

  def Luminosity: Integer = luminosity

  def InitialBlobsNumber: Integer = initialBlobsNumber

  def InitialFoodNumber: Integer = initialFoodNumber

  def InitialObstacleNumber: Integer = initialObstacleNumber

}
