package ktlo.psyssim.model

import javafx.animation.PathTransition
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ListChangeListener
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.ArcTo
import javafx.scene.shape.MoveTo
import tornadofx.*
import kotlin.math.PI
import kotlin.math.sqrt

class PlanetarySystem: Fragment() {

    private var model: AstronomicalObject? = null

    val frequencyProperty = SimpleDoubleProperty()
    var frequency by frequencyProperty

    private val onJump = mutableListOf<(Double)->Unit>()
    private val onPause = mutableListOf<(Boolean)->Unit>()

    fun jump(time: Double) {
        onJump.forEach { it(time) }
    }

    fun pause(value: Boolean) {
        onPause.forEach { it(value) }
    }

    lateinit var onPlanetSelection: (AstronomicalObject) -> Unit

    fun onSelection(action: (AstronomicalObject) -> Unit) {
        onPlanetSelection = action
    }

    override val root = group {
        frequency = 20.0
        //buildSystem(this, model)
    }

    var star: AstronomicalObject
    get() = model!!
    set(value) {
        val oldModel = model
        if (oldModel === value)
            return
        if (oldModel !== null) {
            oldModel.associatedGroup.children.clear()
            onJump.clear()
            onPause.clear()
        }
        model = value
        buildSystem(root, value)
    }

    private fun Group.subPlanet(planetModel: AstronomicalObject) {
        var v = Calculator(planetModel)

        val orbit = path {
            moveTo(v.x, v.y)
            arcTo(v.a, v.b, v.alpha / PI * 180.0, 1 + v.x, v.y, true)
            closepath()
            stroke = Color.DODGERBLUE
            strokeDashArray.setAll(5.0, 5.0)
        }
        planetModel.associatedOrbit = orbit

        val planet = group {
            this.sceneToLocal(.0, .0)
            buildSystem(this, planetModel)
            isFocusTraversable = true
        }

        val transition = PathTransition().apply {
            val scale = sqrt(v.a*v.a*v.a)/frequency
            duration = scale.seconds
            path = orbit
            node = planet
            orientation = PathTransition.OrientationType.NONE
            cycleCount = Timeline.INDEFINITE
            isAutoReverse = false
            interpolator = KeplerInterpolator(v, planetModel.positionProperty)
            playFrom((planetModel.position * scale).seconds)
        }

        fun whenPathChanged(observable: Observable) {
            v = Calculator(planetModel)
            with (orbit.elements[0] as MoveTo) {
                x = v.x
                y = v.y
            }
            with (orbit.elements[1] as ArcTo) {
                radiusX = v.a
                radiusY = v.b
                xAxisRotation = v.alpha / PI * 180.0
                x = 1.0 + v.x
                y = v.y
            }
            with (transition) {
                stop()
                val time = planetModel.position
                duration = (sqrt(v.a*v.a*v.a)/frequency).seconds
                interpolator = KeplerInterpolator(v, planetModel.positionProperty)
                playFrom((time * duration.toMillis()).millis)
            }
        }

        // Set callbacks
        val frequencyListener = InvalidationListener { _ ->
            with (transition) {
                stop()
                val time = planetModel.position
                duration = (sqrt(v.a*v.a*v.a)/frequency).seconds
                interpolator = KeplerInterpolator(v, planetModel.positionProperty)
                playFrom((time * duration.toMillis()).millis)
            }
        }
        frequencyProperty.addListener(frequencyListener)
        with (planetModel) {
            focusProperty.addListener(::whenPathChanged)
            eProperty.addListener(::whenPathChanged)
            angleProperty.addListener(::whenPathChanged)
            fun whenJump(it: Double) {
                with (transition) {
                    stop()
                    val cycles = it / transition.duration.toSeconds() +
                            planetModel.position
                    val time = if (cycles < 0) 1.0 - (-cycles % 1.0) else cycles % 1.0
                    interpolator = KeplerInterpolator(v, planetModel.positionProperty)
                    playFrom((time * duration.toMillis()).millis)
                }
            }
            fun whenPause(it: Boolean) {
                if (it)
                    transition.pause()
                else
                    transition.play()
            }
            onJump += ::whenJump
            onPause += ::whenPause
            parent!!.children.addListener {
                it: ListChangeListener.Change<out AstronomicalObject> ->
                while (it.next()) {
                    for (each in it.removed) {
                        if (each === planetModel) {
                            frequencyProperty.removeListener(frequencyListener)
                            //if (children.isEmpty()) {
                            //    onJump -= ::whenJump
                            //    onPause -= ::whenPause
                            //}
                        }
                    }
                }
            }
        }
    }

    private fun buildSystem(parent: Group, model: AstronomicalObject) {
        model.associatedGroup = parent
        with (parent) {
            val star = circle(0, 0, model.mass) {
                fill = model.picture.fill
                setOnMouseClicked {
                    onPlanetSelection(model)
                }
            }

            var t = timeline {
                keyframe((2*PI/model.w).seconds) {
                    keyvalue(star.rotateProperty(), 360)
                }
                cycleCount = Timeline.INDEFINITE
            }

            // Set callbacks for the planet or star
            model.massProperty.addListener { _ ->
                star.radius = model.mass
            }
            model.pictureProperty.addListener { _ ->
                star.fill = model.picture.fill
            }
            model.wProperty.addListener { _ ->
                t.stop()
                t = timeline {
                    keyframe((2*PI/model.w).seconds) {
                        keyvalue(star.rotateProperty(), 360)
                    }
                    cycleCount = Timeline.INDEFINITE
                }
            }
            model.children.addListener {
                it: ListChangeListener.Change<out AstronomicalObject> ->
                while (it.next()) {
                    for (each in it.addedSubList) {
                        subPlanet(each)
                    }
                    for (each in it.removed) {
                        each.associatedGroup.removeFromParent()
                        each.associatedOrbit?.removeFromParent()
                    }
                }
            }

            for (planetModel in model.children)
                subPlanet(planetModel)
        }
    }

}