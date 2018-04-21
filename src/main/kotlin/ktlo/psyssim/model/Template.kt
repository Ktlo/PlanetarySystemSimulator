package ktlo.psyssim.model

import ktlo.psyssim.controller.TemplateController
import tornadofx.*

enum class Template(name: String, private val message: String) {
    Inner("template.inner", "description.inner") {
        override val settings: PSSettings
            get() = controller.inner()
    },
    Real("template.real", "description.real") {
        override val settings: PSSettings
            get() = controller.real()
    },
    ;

    protected val controller = find(TemplateController::class)
    private val string = controller.messages[name]
    override fun toString(): String {
        return string
    }
    val description get() = controller.messages[message]
    abstract val settings: PSSettings
}