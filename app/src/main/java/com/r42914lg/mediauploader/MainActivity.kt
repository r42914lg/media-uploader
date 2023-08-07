package com.r42914lg.mediauploader

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.r42914lg.mediauploader.data.local.LocalRepoTest
import com.r42914lg.mediauploader.data.remote.RemoteApiTestImpl
import com.r42914lg.mediauploader.data.remote.RemoteDataSource
import com.r42914lg.mediauploader.sender.Sender
import com.r42914lg.mediauploader.slider.ViewPager
import com.r42914lg.mediauploader.ui.theme.MediaUploaderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaUploaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyAppNavHost()
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

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "picker",
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("picker") {
            ContentPicker(
                onContentSelected = {
                    val uriListWrapper = UriListWrapper(it.toStringList())
                    val json = Uri.encode(Gson().toJson(uriListWrapper))
                    navController.navigate("slider/$json")
                },
            )
        }
        composable(
            route = "slider/{uriListWrapper}",
            arguments = listOf(navArgument("uriListWrapper") { type = UriListWrapperType() })
        ) {
            val v = it.arguments?.getParcelable("uriListWrapper") ?: UriListWrapper(listOf())
            ViewPager(
                uriList = v.uriStrs.toUriList()
            )
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

fun List<Uri>.toStringList(): List<String> = map { it.toString() }
fun List<String>.toUriList(): List<Uri> = map { Uri.parse(it) }

data class UriListWrapper(val uriStrs: List<String>) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createStringArrayList() ?: listOf()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(uriStrs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UriListWrapper> {
        override fun createFromParcel(parcel: Parcel): UriListWrapper {
            return UriListWrapper(parcel)
        }

        override fun newArray(size: Int): Array<UriListWrapper?> {
            return arrayOfNulls(size)
        }
    }
}

class UriListWrapperType : NavType<UriListWrapper>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): UriListWrapper? {
        return bundle.getParcelable(key)
    }
    override fun parseValue(value: String): UriListWrapper {
        return Gson().fromJson(value, UriListWrapper::class.java)
    }
    override fun put(bundle: Bundle, key: String, value: UriListWrapper) {
        bundle.putParcelable(key, value)
    }
}

