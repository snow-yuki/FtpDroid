package simple.ftpdroid

import java.io.IOException
import java.io.InputStream

class ProgressInputStream(private val inputStream: InputStream,
                          private val listener : (progress : Long) -> Unit) : InputStream() {

    private var progress: Long = 0
    private var lastUpdate: Long = 0

    private var closed: Boolean = false

    init {
        this.progress = 0
        this.lastUpdate = 0
        this.closed = false
    }

    @Throws(IOException::class)
    override fun read(): Int {
        val count = inputStream.read()
        return incrementCounterAndUpdateDisplay(count)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val count = inputStream.read(b, off, len)
        return incrementCounterAndUpdateDisplay(count)
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
        if (closed)
            throw IOException("already closed")
        closed = true
    }

    private fun incrementCounterAndUpdateDisplay(count: Int): Int {
        if (count > 0)
            progress += count.toLong()
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate)
        return count
    }

    private fun maybeUpdateDisplay(progress: Long, vLastUpdate: Long): Long {
        var lastUpdate = vLastUpdate
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress
            listener(progress)
        }
        return lastUpdate
    }

    companion object {
        private const val TEN_KILOBYTES = 1024 * 10  //每上传10K返回一次
    }


}
