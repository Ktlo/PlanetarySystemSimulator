package ktlo.psyssim.model

import javafx.animation.Interpolator
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
import kotlin.math.abs
import kotlin.math.sqrt

class PlanetarySystem: Fragment() {

    private var model: AstronomicalObject? = null

    val frequencyProperty = SimpleDoubleProperty()
    var frequency by frequencyProperty

    private val onJump = mutableListOf<(Double)->Unit>()
    private val onPause = mutableListOf<(Boolean)->Unit>()
    private var pause: Boolean = false

    fun jump(time: Double) {
        onJump.forEach { it(time) }
    }

    fun pause(value: Boolean) {
        pause = value
        onPause.forEach { it(value) }
    }

    private lateinit var onPlanetSelection: (AstronomicalObject) -> Unit

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
            pause = false
        }
        model = value
        buildSystem(root, value)
    }

    private fun Group.subPlanet(planetModel: AstronomicalObject) {
        //var v = Calculator(planetModel)
        planetModel.recalculateValues()

        val orbit = path {
            moveTo(planetModel.x, planetModel.y)
            arcTo(planetModel.a, planetModel.b, planetModel.angle / PI * 180.0, 1 + planetModel.x, planetModel.y, true)
            closepath()
            stroke = Color.DODGERBLUE
            val scale = 1.0/root.scaleX
            strokeWidth = scale
            strokeDashArray.setAll(5.0*scale, 5.0*scale)
        }
        planetModel.associatedOrbit = orbit

        val planet = group {
            this.sceneToLocal(.0, .0)
            buildSystem(this, planetModel)
            isFocusTraversable = true
        }

        val transition = PathTransition().apply {
            val a = planetModel.a
            val scale = sqrt(a*a*a)/frequency
            duration = scale.seconds
            path = orbit
            node = planet
            orientation = PathTransition.OrientationType.NONE
            cycleCount = Timeline.INDEFINITE
            isAutoReverse = false
            interpolator = KeplerInterpolator(planetModel)
            playFrom((planetModel.position * scale).seconds)
            if (pause) pause()
        }

        fun whenPathChanged(observable: Observable) {
            planetModel.recalculateValues()
            with (orbit.elements[0] as MoveTo) {
                x = planetModel.x
                y = planetModel.y
            }
            val a = planetModel.a
            with (orbit.elements[1] as ArcTo) {
                radiusX = a
                radiusY = planetModel.b
                xAxisRotation = planetModel.angle / PI * 180.0
                x = 1.0 + planetModel.x
                y = planetModel.y
            }
            with (transition) {
                stop()
                val time = planetModel.position
                duration = (sqrt(a*a*a)/frequency).seconds
                interpolator = KeplerInterpolator(planetModel)
                playFrom((time * duration.toMillis()).millis)
                if (pause) pause()
            }
        }

        // Set callbacks
        val frequencyListener = InvalidationListener { _ ->
            with (transition) {
                stop()
                val a = planetModel.a
                val time = planetModel.position
                duration = (sqrt(a*a*a)/frequency).seconds
                interpolator = KeplerInterpolator(planetModel)
                playFrom((time * duration.toMillis()).millis)
                if (pause) pause()
            }
        }
        frequencyProperty.addListener(frequencyListener)
        root.scaleXProperty().addListener { _, _, newValue ->
            val scale = 1.0/newValue.toDouble()
            orbit.strokeWidth = scale
            orbit.strokeDashArray[0] = 5.0*scale
            orbit.strokeDashArray[1] = 5.0*scale
        }
        with (planetModel) {
            aProperty.addListener(::whenPathChanged)
            bProperty.addListener(::whenPathChanged)
            angleProperty.addListener(::whenPathChanged)
            fun whenJump(it: Double) {
                with (transition) {
                    stop()
                    val cycles = it / transition.duration.toSeconds() +
                            planetModel.position
                    val time = if (cycles < 0) 1.0 - (-cycles % 1.0) else cycles % 1.0
                    interpolator = KeplerInterpolator(planetModel)
                    playFrom((time * duration.toMillis()).millis)
                    if(pause) pause()
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
                val w = model.w
                keyframe((2*PI/abs(w)).seconds) {
                    keyvalue(star.rotateProperty(), 360,
                            if (w < .0) ReverseInterpolator
                            else Interpolator.LINEAR)
                }
                cycleCount = Timeline.INDEFINITE
            }

            onPause += {
                if (it) t.pause()
                else t.play()
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
                val w = model.w
                if (w != .0)
                    t = timeline {
                        keyframe((2*PI/abs(w)).seconds) {
                            star.rotate = .0
                            keyvalue(star.rotateProperty(), 360,
                                    if (w < .0) ReverseInterpolator
                                    else Interpolator.LINEAR)
                        }
                        cycleCount = Timeline.INDEFINITE
                        if (!pause) play()
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