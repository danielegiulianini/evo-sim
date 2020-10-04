package evo_sim.view

import evo_sim.model.Environment
import scalafx.beans.binding.Bindings
import scalafx.event.Event
import scalafx.scene.control.{Button, TextField, TextFormatter}
import scalafx.util.converter.NumberStringConverter
import scalafxml.core.macros.sfxml

@sfxml
class FXPresenter(blobTextField: TextField, startButton: Button) {

  blobTextField.setTextFormatter(new TextFormatter(new NumberStringConverter()))

  startButton.disableProperty().bind(
    Bindings.createBooleanBinding(
      () => blobTextField.text.value.trim.isEmpty,
      blobTextField.text))

  def onStart(event: Event): Unit = {
    userInput.environment.success(new Environment(
      temperature = 30,
      luminosity = 50,
      initialBlobsNumber = blobTextField.text.value.toInt,
      initialFoodNumber = 0,
      initialObstacleNumber = 0))
  }

}
