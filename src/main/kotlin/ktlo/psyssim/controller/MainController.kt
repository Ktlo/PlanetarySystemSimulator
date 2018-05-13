package ktlo.psyssim.controller

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextFormatter
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import ktlo.psyssim.UnclosableOutputStream
import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.PlanetPicture
import ktlo.psyssim.model.SolarSystem
import ktlo.psyssim.pipe
import ktlo.psyssim.view.MainView
import ktlo.psyssim.view.PlanetSettingsView
import ktlo.psyssim.view.StartMenuView
import tornadofx.*
import java.io.File
import java.io.FileFilter
import java.time.LocalDateTime
import java.util.*
import java.util.function.UnaryOperator
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class MainController: Controller() {
    val mainView: MainView by inject()
    private val startMenuView: StartMenuView by inject()
    private val planetSettingsView: PlanetSettingsView by inject()

    lateinit var settings: PSSettings

    val protocol = "psyssim:"
    private val userHome = File(System.getProperty("user.home"))
    private val dataDirectory = File(userHome,
            (if (System.getProperty("os.name").contains("windows", true))
                "/AppData/Roaming/" else "") + ".psyssim/")
    val contentDirectory = File(dataDirectory, "content/")
    val savesDirectory = File(dataDirectory, "saves/")

    val programName = messages["program.name"]

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
        mainView.title = "$programName - ${model.name}"
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
            val id = UUID.randomUUID().toString()
            var imageFile: File
            do {
                imageFile = File(contentDirectory, id)
            } while (imageFile.exists())
            selected.copyTo(imageFile)
            planet.picture("$protocol$id")
            planetSettingsView.pictureView.image = planet.picture.image
        }
    }

    fun delete(name: String) {
        val file = File(savesDirectory, "$name.json")
        val model = loadJsonModel<PSSettings>(file.inputStream())
        recursivelyDeleteImages(model.star)
        file.delete()
    }

    fun recursivelyDeleteImages(model: AstronomicalObject) {
        val picture = model.picture
        if (picture is PlanetPicture.ImagePlanetPicture) {
            picture.uri = ""
        }
        model.children.forEach { recursivelyDeleteImages(it) }
    }

    fun export() {
        val chooser = FileChooser().apply {
            initialDirectory = userHome
            val extension = FileChooser.ExtensionFilter("Planetary System", "*.pss")
            extensionFilters.addAll(extension)
            selectedExtensionFilter = extension
            title = messages["exportDialog"]
        }
        val outputFile = chooser.showSaveDialog(mainView.currentWindow) ?: return
        try {
            val output = ZipOutputStream(outputFile.outputStream())
            output.putNextEntry(ZipEntry("${settings.name}.json"))
            settings.save(UnclosableOutputStream(output))
            output.closeEntry()
            recursivelyPutImages(settings.star, output)
            output.close()
        } catch (e: Exception) {
            e.printStackTrace(System.err)
            alert(Alert.AlertType.ERROR, messages["exportError.header"], e.localizedMessage, title = programName)
        }
    }

    fun import() {
        val chooser = FileChooser().apply {
            initialDirectory = userHome
            val extension = FileChooser.ExtensionFilter("Planetary System", "*.pss")
            extensionFilters.addAll(extension)
            selectedExtensionFilter = extension
            title = messages["exportDialog"]
        }
        val inputFile = chooser.showOpenDialog(startMenuView.currentWindow) ?: return
        try {
            val input = ZipInputStream(inputFile.inputStream())
            var entry = input.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val filename = entry.name
                    val parentDirectory =
                    if (filename.endsWith(".json"))
                        savesDirectory
                    else
                        contentDirectory
                    val output = File(parentDirectory, filename).outputStream()
                    output.pipe(input)
                    output.close()
                }
                input.closeEntry()
                entry = input.nextEntry
            }
            alert(Alert.AlertType.INFORMATION, messages["importSuccess.header"], messages["importSuccess.message"],
                    title = programName)
        } catch (e: Exception) {
            e.printStackTrace(System.err)
            alert(Alert.AlertType.ERROR, messages["importError.header"], e.localizedMessage, title = programName)
        }
    }

    private fun recursivelyPutImages(planet: AstronomicalObject, output: ZipOutputStream) {
        val pic = planet.picture
        if (pic is PlanetPicture.ImagePlanetPicture) {
            val uri = pic.uri
            if (uri.startsWith(protocol)) {
                val fileName = uri.substring(protocol.length)
                output.putNextEntry(ZipEntry(fileName))
                val input = File(contentDirectory, fileName).inputStream()
                output.pipe(input)
                input.close()
                output.closeEntry()
            }
        }
        planet.children.forEach { recursivelyPutImages(it, output) }
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

                val filter = UnaryOperator<TextFormatter.Change> {
                    if (it.isReplaced)
                        if(it.text.matches(Regex("""[^\w.а-яА-ЯЁё ]""")))
                            it.text = it.controlText.substring(it.rangeStart, it.rangeEnd)
                    if (it.isAdded) {
                        if (it.text.matches(Regex("""[^\w.а-яА-ЯЁё ]"""))) {
                            it.text = ""
                        }
                    }
                    it
                }
                editor.textFormatter = TextFormatter<TextFormatter.Change>(filter)
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