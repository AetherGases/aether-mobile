package com.aether.application.feature.profile.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

private const val MAX_AVATAR_SIZE_PX = 512
private const val JPEG_QUALITY = 85

/** Decodes a picked or captured photo into a small JPEG, keeping uploads light. */
class AvatarImageProcessor(
    private val context: Context
) {
    suspend fun toJpeg(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val (width, height) = info.size.let { it.width to it.height }
            val scale = MAX_AVATAR_SIZE_PX.toFloat() / maxOf(width, height)
            if (scale < 1f) {
                decoder.setTargetSize((width * scale).toInt(), (height * scale).toInt())
            }
            // Software bitmaps can be compressed; hardware ones cannot
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }

        ByteArrayOutputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
            bitmap.recycle()
            output.toByteArray()
        }
    }
}
