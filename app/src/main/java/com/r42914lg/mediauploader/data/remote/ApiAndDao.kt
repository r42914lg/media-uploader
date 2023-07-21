package com.r42914lg.mediauploader.data.remote

import com.r42914lg.mediauploader.domain.DeleteResult
import com.r42914lg.mediauploader.domain.UploadResult
import com.r42914lg.mediauploader.utils.Result
import com.r42914lg.mediauploader.utils.runOperationCatching
import kotlinx.coroutines.delay
import okhttp3.MultipartBody

interface RemoteApi {
    suspend fun uploadFile(part: MultipartBody.Part): UploadResult
    suspend fun deleteFile(id: Int): DeleteResult
}

class RemoteDataSource (
    private val api: RemoteApi,
) {
    suspend fun uploadFile(part: MultipartBody.Part): Result<UploadResult, Throwable> =
        runOperationCatching {
            api.uploadFile(part)
        }

    suspend fun deleteFile(id: Int): Result<DeleteResult, Throwable> =
        runOperationCatching {
            api.deleteFile(id)
        }
}

class RemoteApiTestImpl : RemoteApi {

    @Volatile
    private var i = 0

    override suspend fun uploadFile(part: MultipartBody.Part): UploadResult {
        val resToReturn  = UploadResult(i != 3, ++i)
        delay(i * DELAY)

        return resToReturn
    }

    override suspend fun deleteFile(id: Int): DeleteResult {
        return DeleteResult(true)
    }

    companion object {
        const val DELAY = 5000L
    }
}