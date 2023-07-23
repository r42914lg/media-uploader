package com.r42914lg.mediauploader.sender

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.r42914lg.mediauploader.data.local.LocalRepoTest
import com.r42914lg.mediauploader.data.remote.RemoteApiTestImpl
import com.r42914lg.mediauploader.data.remote.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class UploadWork(private val appContext: Context, workerParams: WorkerParameters)
: CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())

        val uriStrArray = inputData.getStringArray(KEY_URIS) ?: return Result.failure()
        val uriList = toPersistableUri(uriStrArray)

        println("URIs parsed ${uriList.size}")

        val res = Sender(
            ctx = appContext,
            dao = RemoteDataSource(RemoteApiTestImpl()),
            localUriRepo = LocalRepoTest(),
            uploadScope = CoroutineScope(SupervisorJob()),
            cleanUpScope = CoroutineScope(Job()),
        ).send(uriList)

        return if (res)
            Result.success()
        else
            Result.failure()
    }

    private fun toPersistableUri(uriStrArray: Array<out String>): List<Uri> {
        val uriList = mutableListOf<Uri>()
        for (uriStr in uriStrArray) {
            val uri = Uri.parse(uriStr)
            appContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            uriList += uri
        }
        return uriList
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    private fun createNotification() : Notification {
        createNotificationChannel()

        return Notification.Builder(appContext, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Message")
            .build()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_URIS = "KEY_URI_LIST_AS STRING_ARRAY"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "TEST_CHANNEL_ID_1"
        const val CHANNEL_NAME = "TEST_CHANNEL_1"
        const val CHANNEL_DESC = "Test channel"
    }
}
