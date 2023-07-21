package com.r42914lg.mediauploader.sender

import android.content.Context
import android.net.Uri
import com.r42914lg.mediauploader.data.local.Repository
import com.r42914lg.mediauploader.data.remote.RemoteDataSource
import com.r42914lg.mediauploader.utils.doOnError
import com.r42914lg.mediauploader.utils.doOnSuccess
import kotlinx.coroutines.*
import okhttp3.MultipartBody

class Sender(
    private val ctx: Context,
    private val dao: RemoteDataSource,
    private val localUriRepo: Repository,
    private val uploadScope: CoroutineScope,
    private val cleanUpScope: CoroutineScope,
) {

    suspend fun send(uriList: List<Uri>): Boolean {
        var loadedCount = uriList.size
        var errorFlag = false
        val loadedIds = mutableListOf<Int>()
        val jobs = mutableListOf<Job>()

        for (currUri in uriList) {
            val stream = ctx.contentResolver.openInputStream(currUri) ?: continue
            val request = UploadStreamRequestBody(
                uri = currUri,
                mediaType = "image/*",
                inputStream = stream,
                onUploadProgress = ::reportProgress
            )

            val filePart = MultipartBody.Part.createFormData(
                "file",
                "test.jpg",
                request
            )

            localUriRepo.saveUri(currUri)

            jobs += uploadScope.launch {
                dao.uploadFile(filePart).doOnError {
                    errorFlag = true
                }.doOnSuccess {
                    if (it.status) {
                        loadedCount--
                        loadedIds.add(it.id)
                        println("Upload succeeded for Uri = $currUri load ID --> ${it.id}")
                    } else
                        errorFlag = true
                }

                localUriRepo.removeUri(currUri)

                if (errorFlag) {
                    println("Upload failed for Uri = $currUri")
                    uploadScope.cancel()
                }
            }
        }

        jobs.joinAll()

        if (errorFlag || loadedCount != 0) {
            cleanUpScope.launch {
                cleanUpOnFailure(loadedIds)
            }
        }

        return loadedCount == 0
    }

    private suspend fun cleanUpOnFailure(loadedIds: List<Int>) {
        loadedIds.forEach { id ->
            dao.deleteFile(id).doOnError {
                println("Clean-up failed for id = $id")
            }.doOnSuccess {
                println("Clean-up succeeded for id = $id")
            }
        }
    }
}

fun reportProgress(uri: Uri, progress: Int) {
    println("Uploading Uri: $uri,  progress --> $progress")
}



