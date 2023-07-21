package com.r42914lg.mediauploader.data.local

import android.net.Uri

interface Repository {
    fun saveUri(uri: Uri)
    fun removeUri(uri: Uri)
    fun checkUriInProgress(uri: Uri): Boolean
}

class LocalRepoTest : Repository {
    override fun saveUri(uri: Uri) {

    }

    override fun removeUri(uri: Uri) {

    }

    override fun checkUriInProgress(uri: Uri): Boolean {
        return false
    }
}

