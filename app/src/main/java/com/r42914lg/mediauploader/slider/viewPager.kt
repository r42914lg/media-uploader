package com.r42914lg.mediauploader.slider

import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewPager(
    uriList: List<Uri>,
) {
    val ctx = LocalContext.current
    val cR = ctx.contentResolver
    val mime: MimeTypeMap = MimeTypeMap.getSingleton()

    fun checkMimeType(uri: Uri): Int {
        var retVal = 0

        if (mime.getExtensionFromMimeType(cR.getType(uri)) == "jpg")
            retVal = 1
        else if (mime.getExtensionFromMimeType(cR.getType(uri)) == "mp4")
            retVal = 2

        return retVal
    }

    val playersByPage = remember { mutableMapOf<Int, ExoPlayer>() }

    fun onPlayerCreated(page: Int, exoPlayer: ExoPlayer) {
        playersByPage[page] = exoPlayer
    }

    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            playersByPage.forEach {
                if (it.key == page) {
                    it.value.seekTo(0, 0L)
                    it.value.prepare()
                    it.value.play()
                } else
                    it.value.stop()
            }
        }
    }

    HorizontalPager(
        pageCount = uriList.size,
        state = pagerState,
        beyondBoundsPageCount = uriList.size - 1,
    ) { page ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            contentAlignment = Alignment.Center,
        ) {
            val uri = uriList[page]
            when (checkMimeType(uri)) {
                0 -> Text("Preview not supported")
                1 -> ImageView(uri)
                2 -> VideoPlayer(uri, page, ::onPlayerCreated)
            }
        }
    }
}
