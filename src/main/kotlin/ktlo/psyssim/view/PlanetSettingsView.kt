package ktlo.psyssim.view

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.stage.StageStyle
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.PlanetPicture
import ktlo.psyssim.model.SolarSystem
import tornadofx.*
import java.io.File
import kotlin.math.PI
import kotlin.math.sqrt

class PlanetSettingsView : View() {
    override val root: ScrollPane by fxml()

    private val controller: MainController by inject()

    private val nameField: TextField by fxid()
    val pictureView: ImageView by fxid()
    private val eField: TextField by fxid()
    private val cField: TextField by fxid()
    private val aField: TextField by fxid()
    private val bField: TextField by fxid()
    private val angleField: TextField by fxid()
    private val massField: TextField by fxid()
    private val wField: TextField by fxid()
    private val removePlanetButton: Button by fxid()

    var selectedPlanet: AstronomicalObject? = null

    fun selectPlanet(planet: AstronomicalObject) {
        selectedPlanet = planet
        with (planet) {
            nameField.text = name
            pictureView.image = picture.image
            eField.text = e.toString()
            cField.text = focus.toString()
            aField.text = a.toString()
            bField.text = b.toString()
            angleField.text = angle.toString()
            massField.text = mass.toString()
            wField.text = w.toString()

            val disabled = planet.parent == null
            eField.isDisable = disabled
            cField.isDisable = disabled
            aField.isDisable = disabled
            bField.isDisable = disabled
            angleField.isDisable = disabled
            removePlanetButton.isDisable = disabled
        }
    }

    @UIMethod
    fun onNewName() {
        selectedPlanet!!.name = nameField.text
    }

    @UIMethod
    fun onNewImage() {
        val lookView = find(PlanetLookView::class)
        lookView.planet = selectedPlanet!!
        val listener = InvalidationListener {
            pictureView.image = lookView.picture.image
        }
        lookView.picture.imageProperty().addListener(listener)
        val stage = lookView.openModal(StageStyle.UTILITY, resizable = false)!!
        stage.onCloseRequest = EventHandler { lookView.picture.imageProperty().removeListener(listener) }
    }

    private fun SimpleDoubleProperty.update(
            field: StringProperty,
            assertion: (Double) -> Boolean): Boolean {
        try {
            val number = field.get().toDouble()
            if (assertion(number))
                this.set(number)
            else
                throw Exception()
            return true

        } catch (e: Exception) {
            field.set(this.get().toString())
        }
        return false
    }

    private fun updateEC() {
        val selected = selectedPlanet!!
        eField.text = selected.e.toString()
        cField.text = selected.focus.toString()
    }

    @UIMethod
    fun onNewA() {
        val selected = selectedPlanet!!
        val result = selected.aProperty.update(aField.textProperty()) {
            it >= selected.b
        }
        if (result)
            updateEC()
    }

    @UIMethod
    fun onNewB() {
        val selected = selectedPlanet!!
        val result = selected.bProperty.update(bField.textProperty()) {
            it in .0..selected.a
        }
        if (result)
            updateEC()
    }

    @UIMethod
    fun onNewEC() {
        val selected = selectedPlanet!!
        try {
            val e = eField.text.toDouble()
            val c = cField.text.toDouble()
            assert(e > .0001 && c > 0.0001)
            val a = c/e
            val b = sqrt(a*a - c*c)
            aField.text = a.toString()
            bField.text = b.toString()
            selected.a = a
            selected.b = b
        } catch (exception: Exception) {
            eField.text = selected.e.toString()
            cField.text = selected.focus.toString()
        }
    }

    @UIMethod
    fun onECSelection() {
        val disabled = with(selectedPlanet ?: return) { e == .0 || focus == .0 }
        eField.isDisable = disabled
        cField.isDisable = disabled
    }

    @UIMethod
    fun onNewAngle() {
        selectedPlanet!!.angleProperty.update(angleField.textProperty()) {
            it in -PI/2 + 0.000001 .. PI/2 - 0.000001
        }
    }

    @UIMethod
    fun onNewMass() {
        selectedPlanet!!.massProperty.update(massField.textProperty()) {
            it > .0
        }
    }

    @UIMethod
    fun onNewW() {
        selectedPlanet!!.wProperty.update(wField.textProperty()) {
            true
        }
    }

    @UIMethod
    fun newChildPlanet() {
        val star = selectedPlanet!!
        val planet = star.planet {
            mass = star.mass * 0.6
            e = 0.4
            focus = 40.0
            picture(SolarSystem.Planet)
        }
        selectPlanet(planet)
    }

    @UIMethod
    fun removePlanet() {
        val planet = selectedPlanet!!
        planet.parent!!.children.remove(planet)
        controller.recursivelyDeleteImages(planet)
        closeSettings()
    }

    @UIMethod
    fun closeSettings() {
        controller.mainView.graphicalLook.left = null
    }
}
