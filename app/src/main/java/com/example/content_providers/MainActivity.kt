package com.example.content_providers

import android.content.ContentUris
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.content_providers.ui.theme.ContentProvidersTheme
import java.util.Calendar


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel>()
//    InputStream: Used to read data from a source like a file, network, or keyboard.
//    OutputStream: Used to write data to a destination like a file, network, or display.

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
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
            viewModel.updateImages(images)

        }
        setContent {
            ContentProvidersTheme {

            }
        }
    }

}

data class Image(
    val id : Long,
    val name : String,
    val uri : Uri
)