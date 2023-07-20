package com.r42914lg.mediauploader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.r42914lg.mediauploader.sender.Sender
import com.r42914lg.mediauploader.ui.theme.MediaUploaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaUploaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ContentPicker() {}
                }
            }
        }
    }
}

@Composable
fun ContentPicker(
    onNavigateToViewer: () -> Unit,
) {
    val ctx = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) {
        Sender(ctx).send(it)
        onNavigateToViewer()
    }

    Button(onClick = {
        launcher.launch("*/*")
    }) {
        Text(text = "Pick image")
    }
}