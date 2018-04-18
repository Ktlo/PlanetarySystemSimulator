package ktlo.psyssim.controller

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.PlanetPicture
import ktlo.psyssim.model.SolarSystem
import ktlo.psyssim.view.MainView
import ktlo.psyssim.view.PlanetSettingsView
import tornadofx.*
import java.io.File
import java.io.FileFilter
import java.time.LocalDateTime
import java.util.*

class MainController: Controller() {
    val mainView: MainView by inject()
    val planetSettingsView: PlanetSettingsView by inject()

    lateinit var settings: PSSettings

    private val userHome = File(System.getProperty("user.home"))
    private val dataDirectory = File(userHome,
            (if (System.getProperty("os.name").contains("windows", true))
                "/AppData/Roaming/" else "") + ".psyssim/")
    private val contentDirectory = File(dataDirectory, "content/")
    val savesDirectory = File(dataDirectory, "saves/")

    init {
        contentDirectory.mkdirs()
        savesDirectory.mkdirs()
    }

    fun load(name: String) {
        val file = File(savesDirectory, "$name.json")
        load(loadJsonModel<PSSettings>(file.inputStream()))
    }

    fun load(model: PSSettings) {
        settings = model
        mainView.title = "Planetary System Simulator - ${model.name}"
        mainView.planetarySystem.star = model.star
    }

    fun save() {
        val file = File(savesDirectory, settings.name + ".json")
        settings.save(file.outputStream())
    }

    val planetarySystemList: List<String>
        get() = savesDirectory.listFiles(FileFilter { it.name.endsWith(".json") })
                .map { with (it.name) { substring(0, length - 5) } }

    fun imageLoader(planet: AstronomicalObject) {
        val chooser = FileChooser().apply {
            initialDirectory = userHome
            val extension = FileChooser.ExtensionFilter("Image", "*.jpg", "*.png")
            extensionFilters.addAll(extension)
            selectedExtensionFilter = extension
            title = messages["imageFileDialog"]
        }
        val selected = chooser.showOpenDialog(mainView.currentWindow)
        if (selected != null) {
            val imageFile = File(contentDirectory, UUID.randomUUID().toString())
            selected.copyTo(imageFile)
            val uri = "file:" + imageFile.absolutePath
            val picture = Image(uri)
            with (planetSettingsView.pictureView) {
                image = picture
            }
            val oldPicture = planet.picture
            if (oldPicture is PlanetPicture.ImagePlanetPicture) {
                val p = oldPicture.uri
                if (p.startsWith("file:${contentDirectory.absolutePath}"))
                    File(p.substring(5)).delete()
            }
            planet.picture(uri)
        }
    }

    fun createFromTemplate(template: PSSettings, view: View? = null) {
        while (true) {
            val titleText = messages["new.title"]
            val dialog = TextInputDialog(titleText).apply {
                graphic = ImageView(Image(SolarSystem.Sun.uri)).apply {
                    fitWidth = 55.0
                    fitHeight = 55.0
                }
                title = titleText
                headerText = ""
                contentText = messages["new.contentText"]
            }
            val result = dialog.showAndWait()
            if (result.isPresent) {
                val filename = result.get()
                if (File(savesDirectory, "$filename.json").exists()) {
                    val confirmationResult = Alert(Alert.AlertType.CONFIRMATION).apply {
                        title = titleText
                        headerText = messages["warn.headerText"]
                        contentText = messages["warn.contentText"]
                    }.showAndWait()
                    if (confirmationResult.get() != ButtonType.OK)
                        continue
                }
                template.name = filename
                template.timestamp = LocalDateTime.now()
                load(template)
                if (view != null) {
                    primaryStage.width = 850.0
                    primaryStage.height = 500.0
                    primaryStage.isResizable = true
                    view.replaceWith(MainView::class, ViewTransition.Fade(1.seconds))
                }
                break
            }
            else
                break
        }
    }

}