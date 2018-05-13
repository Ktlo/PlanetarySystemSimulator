package ktlo.psyssim

import java.io.InputStream
import java.io.OutputStream

fun printErr(message: String?) = System.err.println(message)

fun OutputStream.pipe(input: InputStream) {
    val n = 1024
    val buffer = ByteArray(n)
    var read = input.read(buffer)
    while (read > 0) {
        write(buffer, 0, read)
        read = input.read(buffer)
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UIMethod