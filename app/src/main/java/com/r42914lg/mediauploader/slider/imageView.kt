package com.r42914lg.mediauploader.slider

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ImageView(
    uri: Uri,
) {
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val source = ImageDecoder.createSource(context.contentResolver, uri)
    
    if (bitmap.value == null) {
        bitmap.value = ImageDecoder.decodeBitmap(source)
    }

    bitmap.value?.let { btm ->
        Image(
            bitmap = btm.asImageBitmap(),
            contentDescription = null,
        )
    }
}
