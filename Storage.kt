package com.mindbyromanzanoni.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.mindbyromanzanoni.R
import java.io.File
import java.io.FileOutputStream

/**
 * Getting image uri to save camera captured image
 * @param context
 * */
fun getUriForCamera(
    context: Context
): Uri? {
    return try {
        val mediaUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageName = "${context.getString(R.string.app_name)}-${
            System.currentTimeMillis().toString().takeLast(5)
        }"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }

        context.contentResolver.insert(mediaUri, contentValues)

    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}

/**
 * Getting file from uri
 * @param context
 * @param uri used to get file
 * @param file callback of the file get from uri
 * */
fun getFileFromUri(
    context: Context,
    uri: Uri,
    file: (file: File) -> Unit
) {
    try {
        context.contentResolver.openInputStream(uri).use { inputStream ->
            getFileName(
                uri = uri,
                context = context
            ) { fileName ->
                val cacheFile = File(context.cacheDir, fileName)
                FileOutputStream(cacheFile).use { outPutStream ->
                    inputStream?.copyTo(outPutStream)
                }
                file.invoke(cacheFile)
            }
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

/**
 * Getting file name from uri
 * @param uri uri to get file name
 * @param context
 * @param fileName filename callback
 */
@SuppressLint("Range")
fun getFileName(
    uri: Uri,
    context: Context,
    fileName: (name: String) -> Unit
) {
    return try {
        context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        ).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                fileName.invoke(name)
            } else fileName.invoke("")
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}