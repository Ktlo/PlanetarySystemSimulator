package ktlo.psyssim

import java.io.OutputStream

class UnclosableOutputStream(private val output: OutputStream): OutputStream() {
    override fun write(b: Int) {
        output.write(b)
    }

    override fun write(b: ByteArray?) {
        output.write(b)
    }

    override fun write(b: ByteArray?, off: Int, len: Int) {
        output.write(b, off, len)
    }

    override fun close() {

    }

    override fun flush() {
        
    }
}