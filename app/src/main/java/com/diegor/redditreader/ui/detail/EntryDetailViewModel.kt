package com.diegor.redditreader.ui.detail

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.diegor.redditreader.R
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.ui.util.CaptureImageUtil
import com.diegor.redditreader.util.CoroutinesDispatcherProvider
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSources
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


/**
 *  Not doing much ATM. We add this as a starting point. This is were would put all our detail related stuff.
 */
class EntryDetailViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _entry = MutableLiveData<DetailUiModel>()
    val entry: LiveData<DetailUiModel>
        get() = _entry

    fun setEntry(entry: Entry) = viewModelScope.launch(dispatcherProvider.computation) {
        val image = if (entry.thumbnailWidth != null && entry.thumbnailHeight != null && entry.thumbnail != null) {
            ImageUiModel(
                width = entry.thumbnailWidth,
                height = entry.thumbnailHeight,
                url = entry.thumbnail
            )
        } else null

        val uiModel = DetailUiModel(entry.title, entry.author, image)

        withContext(dispatcherProvider.main) {
            _entry.value = uiModel
        }
    }

    fun saveImage(url: String) = viewModelScope.launch(dispatcherProvider.computation) {
        val imageRequest = ImageRequest.fromUri(url)
        val imagePipeline = Fresco.getImagePipeline()

        val dataSource: DataSource<CloseableReference<CloseableImage>> =
            imagePipeline.fetchDecodedImage(imageRequest, null)
        try {
            val result =
                DataSources.waitForFinalResult<CloseableReference<CloseableImage>>(dataSource)
            if (result != null) {
                val bitmap = result.get() as? CloseableBitmap
                bitmap?.underlyingBitmap?.let {
                    addImageToMediaContent(appContext, it)
                }
            }
        } finally {
            dataSource.close()
        }
    }

    private fun addImageToMediaContent(context: Context, bitmap: Bitmap) = viewModelScope.launch {
        withContext(dispatcherProvider.io) {
            //2
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val dirDest =
                File(Environment.DIRECTORY_PICTURES, context.getString(R.string.app_name))
            dirDest.mkdir()

            val date = System.currentTimeMillis()
            val extension = "jpeg"
            //3
            val newImage = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$date.$extension")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/$extension")
                put(MediaStore.MediaColumns.DATE_ADDED, date)
                put(MediaStore.MediaColumns.DATE_MODIFIED, date)
                put(MediaStore.MediaColumns.SIZE, bitmap.byteCount)
                put(MediaStore.MediaColumns.WIDTH, bitmap.width)
                put(MediaStore.MediaColumns.HEIGHT, bitmap.height)
            }
            val newImageUri = context.contentResolver.insert(collection, newImage)
            context.contentResolver.openOutputStream(newImageUri!!, "w").use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            newImage.clear()
        }
    }

    private suspend fun saveBitmap(context: Context, bitmap: Bitmap) {
        CaptureImageUtil.saveBitmap(context, bitmap)
    }

    data class DetailUiModel(
        val title: String,
        val author: String,
        val image: ImageUiModel?
    )

    data class ImageUiModel(
        val width: Int,
        val height: Int,
        val url: String
    )
}