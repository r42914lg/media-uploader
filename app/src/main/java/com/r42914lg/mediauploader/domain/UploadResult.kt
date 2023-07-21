package com.r42914lg.mediauploader.domain

data class UploadResult (
    val status: Boolean,
    val id: Int,
)

data class DeleteResult (
    val status: Boolean,
)