package com.r42914lg.mediauploader

import android.net.Uri
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
import androidx.lifecycle.lifecycleScope
import com.r42914lg.mediauploader.data.local.LocalRepoTest
import com.r42914lg.mediauploader.data.remote.RemoteApiTestImpl
import com.r42914lg.mediauploader.data.remote.RemoteDataSource
import com.r42914lg.mediauploader.sender.Sender
import com.r42914lg.mediauploader.ui.theme.MediaUploaderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaUploaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ContentPicker(
                        onContentSelected = ::sendAsync
                    )
                }
            }
        }
    }

    private fun sendAsync(uris: List<Uri>) {
        lifecycleScope.launch {
            val res = Sender(
                ctx = application,
                dao = RemoteDataSource(RemoteApiTestImpl()),
                localUriRepo = LocalRepoTest(),
                uploadScope = CoroutineScope(SupervisorJob()),
                cleanUpScope = CoroutineScope(Job()),
            ).send(uris)
            println("Bucket result = $res")
        }
    }

    private fun sendViaWorker(uris: List<Uri>) {
        lifecycleScope.launch {
            val res = Sender(
                ctx = application,
                dao = RemoteDataSource(RemoteApiTestImpl()),
                localUriRepo = LocalRepoTest(),
                uploadScope = CoroutineScope(SupervisorJob()),
                cleanUpScope = CoroutineScope(Job()),
            ).send(uris)
            println("Bucket result = $res")
        }
    }
}

@Composable
fun ContentPicker(
    onContentSelected: (List<Uri>) -> Unit,
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) {
        onContentSelected(it)
    }

    Button(onClick = {
        launcher.launch("*/*")
    }) {
        Text(text = "Pick image")
    }
}