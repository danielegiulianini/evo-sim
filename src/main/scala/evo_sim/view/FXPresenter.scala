package evo_sim.view

import javafx.application.Platform
import scalafx.beans.binding.Bindings
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

  def onStart(): Unit = {
    userInput.environment.success(blobTextField.text.value.toInt)
    Platform.exit()
  }

}
