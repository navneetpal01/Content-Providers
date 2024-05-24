package com.example.content_providers

import android.Manifest
import android.content.ContentUris
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.content_providers.ui.theme.ContentProvidersTheme
import java.util.Calendar


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel>()
//    InputStream: Used to read data from a source like a file, network, or keyboard.
//    OutputStream: Used to write data to a destination like a file, network, or display.

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES ),
                0
            )
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
        //Specific columns we want to have from the each image
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        //Using selection we can filter the result
        //If we pass null that means we get all the images passed in the projection

        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?" // Avoid putting your real parameter here as it will make your app vernable of the sql injection
        //Thant's why we use another array here
        val millisYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        val selectionArgs = arrayOf(millisYesterday.toString())
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            //Using cursor we can iterate over this large data set and refer to the specific fields
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val images = mutableListOf<Image>()
            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(Image(id,name,uri))
            }
            Log.d("pokemon", "Let's verify $images")
            viewModel.updateImages(images)

        }
        setContent {
            ContentProvidersTheme {


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    items(viewModel.images){image ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            AsyncImage(
                                model = image.uri,
                                contentDescription = null
                            )
                            Text(text = image.name)

                        }

                    }
                }
            }
        }
    }

}

data class Image(
    val id : Long,
    val name : String,
    val uri : Uri
)