package ktlo.psyssim.model

import ktlo.psyssim.controller.TemplateController
import tornadofx.*

enum class Template {
    Inner {
        override val settings: PSSettings
            get() = controller.inner()
    },
    Outer {
        override val settings: PSSettings
            get() = controller.outer()
    },
    Real {
        override val settings: PSSettings
            get() = controller.real()
    },
    Comets {
        override val settings: PSSettings
            get() = controller.comets()
    };

    protected val controller = find(TemplateController::class)
    private val string = controller.messages["template.$name"]
    override fun toString(): String {
        return string
    }
    val description: String get() = controller.messages["description.$name"]
    abstract val settings: PSSettings
}