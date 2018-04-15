package ktlo.psyssim.model

import kotlin.reflect.KProperty

/*
    Because SimpleDoubleProperty is too complex for KeplerInterpolator
 */
class SuperSimpleDoubleProperty(initial: Double = .0) {
    @JvmField
    var value: Double = initial

    operator fun getValue(thisRef: Any, property: KProperty<*>) = value
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
        this.value = value
    }
}
