package ktlo.psyssim

fun printErr(message: String?) = System.err.println(message)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UIMethod