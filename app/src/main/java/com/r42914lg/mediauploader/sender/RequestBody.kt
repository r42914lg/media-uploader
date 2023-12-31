package com.r42914lg.mediauploader.sender

import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.InputStream

class UploadStreamRequestBody(
    private val uri: Uri,
    private val mediaType: String,
    private val inputStream: InputStream,
    private val onUploadProgress: (Uri, Int) -> Unit,
) : RequestBody() {

    override fun contentLength(): Long = inputStream.available().toLong()

    override fun contentType(): MediaType? = mediaType.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        val contentLength = inputStream.available().toFloat()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        inputStream.use { inputStream ->
            var uploaded = 0
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                sink.write(buffer, 0, read)
                uploaded += read
                onUploadProgress(uri, (100 * uploaded / contentLength).toInt())
            }
        }
    }
}
