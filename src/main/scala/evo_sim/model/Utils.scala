package evo_sim.model

object Utils {

  var index = 0

  def randomValueChange(value: Int, range: Int): Int = {
    value + new java.util.Random().nextInt(range * 2 + 1) - range
  }

  def randomGender(): GenderEnum.Value = {
    new java.util.Random().nextInt(2) match {
      case 0 => GenderEnum.Male
      case 1 => GenderEnum.Female
    }
  }

  def nextValue(): Int = {
    index += 1
    index
  }

}
