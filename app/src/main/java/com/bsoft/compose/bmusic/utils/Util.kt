package com.bsoft.compose.bmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object Util {
    suspend fun loadArtwork(context: Context, artworkUri: Uri?, targetSize: Size): Bitmap? {
        return artworkUri?.let {
            withContext(Dispatchers.IO) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.contentResolver.loadThumbnail(it, targetSize, null)
                    }else{
                        context.contentResolver.openInputStream(it).use { stream -> BitmapFactory.decodeStream(stream) }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    /*suspend fun getAudioArtwork(context: Context, trackId: Long, albumId: Long, targetSize: Size): Bitmap? {
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
    }*/
}