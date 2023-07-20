package com.r42914lg.mediauploader.sender

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class Sender(
    private val ctx: Context,
    private val api: RemoteApi,
    private val cs: CoroutineScope,
    ) {

    fun send(uriList: List<Uri>) {
        for (it in uriList) {
            val stream = ctx.contentResolver.openInputStream(it) ?: continue
            val request = UploadStreamRequestBody(
                mediaType = "image/*",
                inputStream = stream,
                onUploadProgress = ::reportProgress
            )

            val filePart = MultipartBody.Part.createFormData(
                "file",
                "test.jpg",
                request
            )

            cs.launch {
                try {
                    api.uploadFile(filePart)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

interface RemoteApi {
    suspend fun uploadFile(part: MultipartBody.Part)
}

fun reportProgress(progress: Int) {
    println("Upload progress --> $progress")
}



