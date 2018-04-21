package ktlo.psyssim.view

import javafx.scene.control.*
import javafx.scene.layout.Pane
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import tornadofx.*
import java.io.File

class LoadPSView: View() {
    private val controller: MainController by inject()

    override val root: Pane by fxml()

    private val logoFragment = find(LogoFragment::class)
    private val logoPane: Pane by fxid()
    private val psList: ListView<String> by fxid()
    private val deleteButton: Button by fxid()
    private val loadButton: Button by fxid()

    init {
        title = controller.programName
        logoPane += logoFragment
    }

    fun updateList() {
        with (psList) {
            items.setAll(controller.planetarySystemList)
            selectionModel.selectionMode = SelectionMode.SINGLE
            selectionModel.selectedItemProperty().addListener { _ ->
                val selected = selectedItem == null
                deleteButton.isDisable = selected
                loadButton.isDisable = selected
            }
        }
    }

    @UIMethod
    fun delete() {
        val selected = psList.selectedItem ?: return
        alert(Alert.AlertType.WARNING, messages["removeHeader"], messages["removeConfirmation"],
                ButtonType.YES, ButtonType.NO, title = controller.programName) {
            if (it == ButtonType.YES) {
                controller.delete(selected)
                psList.items.remove(selected)
                if (psList.items.isEmpty()) {
                    deleteButton.isDisable = true
                    loadButton.isDisable = true
                }
            }
        }
    }

    @UIMethod
    fun load() {
        val selected = psList.selectedItem
        if (selected != null) {
            controller.load(selected)
            with(primaryStage) {
                width = 850.0
                height = 500.0
                isResizable = true
            }
            replaceWith(MainView::class, ViewTransition.Fade(1.seconds))
        }

    }

    @UIMethod
    fun cancel() {
        replaceWith(StartMenuView::class, ViewTransition.Fade(1.seconds))
    }

}