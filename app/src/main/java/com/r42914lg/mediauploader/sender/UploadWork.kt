package com.r42914lg.mediauploader.sender

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class UploadWork(private val appContext: Context, workerParams: WorkerParameters)
: CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        val uriList = inputData.getStringArray(KEY_URIS) ?: return Result.failure()
        for (uri in uriList) {

        }
        return Result.success()
    }
    //             ctx.contentResolver.takePersistableUriPermission(
//                 it,
//                 Intent.FLAG_GRANT_READ_URI_PERMISSION
//             )
}

const val KEY_URIS = "KEY_URI"
val data = workDataOf(
    //KEY_URI to Uri
)