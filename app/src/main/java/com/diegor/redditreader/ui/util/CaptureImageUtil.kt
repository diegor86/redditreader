package com.diegor.redditreader.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object CaptureImageUtil {

    fun saveBitmap(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream = FileOutputStream("$cachePath/jpeg_${System.currentTimeMillis()}.jpeg") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()
            galleryAddPic(context, cachePath)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun galleryAddPic(context: Context, filePath: File) {
        MediaScannerConnection.scanFile(context, arrayOf(filePath.toString()),
            arrayOf(filePath.name), null)
    }
}