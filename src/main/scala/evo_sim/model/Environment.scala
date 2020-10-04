package evo_sim.model

class Environment(temperature: Integer,
                  luminosity: Integer,
                  initialBlobNumber: Integer,
                  initialFoodNumber: Integer,
                  initialObstacleNumber: Integer) {

  def Temperature: Integer = temperature

  def Luminosity: Integer = luminosity

  def InitialBlobNumber: Integer = initialBlobNumber

  def InitialFoodNumber: Integer = initialFoodNumber

  def InitialObstacleNumber: Integer = initialObstacleNumber

}
