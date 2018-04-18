package ktlo.psyssim.view

import javafx.scene.control.*
import javafx.scene.layout.Pane
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import tornadofx.*
import java.io.File

class LoadPSView: View("Planetary System Simulator") {
    private val controller: MainController by inject()

    override val root: Pane by fxml()

    private val logoFragment = find(LogoFragment::class)
    private val logoPane: Pane by fxid()
    private val psList: ListView<String> by fxid()
    private val deleteButton: Button by fxid()
    private val loadButton: Button by fxid()

    init {
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
        val result = Alert(Alert.AlertType.WARNING, messages["removeConfirmation"], ButtonType.YES, ButtonType.NO)
                .apply { headerText = messages["removeHeader"] }
                .showAndWait()
        if (result.get() == ButtonType.YES) {
            val selected = psList.selectedItem
            File(controller.savesDirectory, "$selected.json").delete()
            psList.items.remove(selected)
            deleteButton.isDisable = true
            loadButton.isDisable = true
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