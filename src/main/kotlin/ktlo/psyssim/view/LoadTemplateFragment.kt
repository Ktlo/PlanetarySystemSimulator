package ktlo.psyssim.view

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.Template
import tornadofx.*

class LoadTemplateFragment: Fragment() {
    val controller: MainController by inject()

    var comboBox: ComboBox<Template> by singleAssign()
    var descriptionArea: TextArea by singleAssign()
    val selectedProperty = SimpleObjectProperty<Template>(Template.Inner)
    val selected: Template by selectedProperty

    var result = false


    override val root = vbox(8, Pos.TOP_CENTER) {

        style {
            padding = box(5.px)
        }

        comboBox = combobox(selectedProperty, Template.values().toList()) {
            maxWidth = Double.POSITIVE_INFINITY
            prefWidth = -1.0
        }

        descriptionArea = textarea(selected.description) {
            isEditable = false
            isWrapText = true
            prefWidth = 225.0
        }

        hbox(5, Pos.CENTER) {
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
        title = controller.programName
        selectedProperty.addListener { _ ->
            descriptionArea.text = selected.description
        }
    }

}