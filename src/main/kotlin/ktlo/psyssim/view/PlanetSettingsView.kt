package ktlo.psyssim.view

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.SolarSystem
import tornadofx.*
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

    private var selectedPlanet: AstronomicalObject? = null

    fun selectPlanet(planet: AstronomicalObject) {
        selectedPlanet = planet
        with (planet) {
            nameField.text = name
            pictureView.image = picture.image
            eField.text = e.toString()
            cField.text = focus.toString()
            updateAB()
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
    fun onNewImage() = controller.imageLoader(selectedPlanet!!)

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

    private fun updateAB() {
        val selected = selectedPlanet!!
        val e = selected.e
        val c = selected.focus
        val a = c / e
        val b = sqrt(a * a - c * c)
        aField.text = a.toString()
        bField.text = b.toString()
    }

    @UIMethod
    fun onNewE() {
        val selected = selectedPlanet!!
        val result = selected.eProperty.update(eField.textProperty()) {
            it in .001..0.9999
        }
        if (result)
            updateAB()
    }

    @UIMethod
    fun onNewC() {
        selectedPlanet!!.focusProperty.update(cField.textProperty()) {
            it > .001
        }
    }

    @UIMethod
    fun onNewAB() {
        try {
            val a = aField.text.toDouble()
            val b = bField.text.toDouble()
            assert(a > 0 && b > 0 && a > b)
            val c = sqrt(a*a - b*b)
            val e = c / a
            cField.text = c.toString()
            eField.text = e.toString()
            with (selectedPlanet!!) {
                this.e = e
                focus = c
            }
        } catch (exception: Exception) {
            val e = selectedPlanet!!.e
            val c = selectedPlanet!!.focus
            val a = c / e
            val b = sqrt(a * a - c * c)
            aField.text = a.toString()
            bField.text = b.toString()
        }
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
            it >= .0
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
        closeSettings()
    }

    @UIMethod
    fun closeSettings() {
        controller.mainView.graphicalLook.left = null
    }
}
