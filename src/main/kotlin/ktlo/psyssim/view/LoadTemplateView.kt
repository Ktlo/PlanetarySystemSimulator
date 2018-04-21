package ktlo.psyssim.view

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.Template
import tornadofx.*

class LoadTemplateView: Fragment() {
    val controller: MainController by inject()

    var comboBox: ComboBox<Template> by singleAssign()
    var descriptionArea: TextArea by singleAssign()
    val selectedProperty = SimpleObjectProperty<Template>(Template.Inner)
    val selected: Template by selectedProperty

    var result = false


    override val root = vbox(8, Pos.TOP_CENTER) {

        comboBox = combobox(selectedProperty, Template.values().toList())

        descriptionArea = textarea(selected.description) {
            isEditable = false
        }

        hbox {
            button(messages["select"]) {
                action {
                    result = true
                    close()
                }
            }
            button(messages["cancel"]) {
                action {
                    result = false
                    close()
                }
            }
        }

    }

    init {
        selectedProperty.addListener { _ ->
            descriptionArea.text = selected.description
        }
    }

}