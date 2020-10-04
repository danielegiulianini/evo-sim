package evo_sim.view

import evo_sim.model.Environment
import scalafx.beans.binding.Bindings
import scalafx.event.Event
import scalafx.scene.control.{Button, TextField, TextFormatter}
import scalafx.util.converter.NumberStringConverter
import scalafxml.core.macros.sfxml

@sfxml
class FXInputPresenter(blobTextField: TextField,
                       foodTextField: TextField,
                       obstacleTextField: TextField,
                       temperatureTextField: TextField,
                       luminosityTextField: TextField,
                       startButton: Button) {

  blobTextField.setTextFormatter(new TextFormatter(new NumberStringConverter()))
  foodTextField.setTextFormatter(new TextFormatter(new NumberStringConverter()))
  obstacleTextField.setTextFormatter(new TextFormatter(new NumberStringConverter()))
  temperatureTextField.setTextFormatter(new TextFormatter(new NumberStringConverter()))
  luminosityTextField.setTextFormatter(new TextFormatter(new NumberStringConverter()))

  startButton.disableProperty().bind(
    Bindings.createBooleanBinding(
      () => blobTextField.text.value.trim.isEmpty,
      blobTextField.text))

  def onStart(event: Event): Unit = {
    userInput.environment.success(new Environment(
      temperature = temperatureTextField.text.value.toInt,
      luminosity = luminosityTextField.text.value.toInt,
      initialBlobNumber = blobTextField.text.value.toInt,
      initialFoodNumber = foodTextField.text.value.toInt,
      initialObstacleNumber = obstacleTextField.text.value.toInt))
  }

}
