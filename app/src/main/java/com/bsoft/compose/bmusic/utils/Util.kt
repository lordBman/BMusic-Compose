package com.bsoft.compose.bmusic.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import androidx.core.net.toUri

object Util {
    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun loadAlbumArt(context: Context, trackId: Long, targetSize: Size): Bitmap? {
        return withContext(Dispatchers.IO) {
            val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                trackId
            )

            try {
                // Android 10 (API 29) and above optimized API
                context.contentResolver.loadThumbnail(trackUri, targetSize, null)
            } catch (e: IOException) {
                e.printStackTrace()
                // Return null or a placeholder asset if compilation/fetch fails
                null
            }
        }
    }

    suspend fun getAudioArtwork(context: Context, trackId: Long, albumId: Long, targetSize: Size): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            loadAlbumArt(context, trackId, targetSize)
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val legacyArtUri = "content://media/external/audio/albumart".toUri()
                    val artworkUri = ContentUris.withAppendedId(legacyArtUri, albumId)
                    context.contentResolver.openInputStream(artworkUri).use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}