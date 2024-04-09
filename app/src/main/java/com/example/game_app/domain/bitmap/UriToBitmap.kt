package com.example.game_app.domain.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.game_app.data.common.Adapter
import java.io.FileNotFoundException

class UriToBitmap(private val context: Context) : Adapter<Uri, Bitmap?> {
    override fun adapt(t: Uri): Bitmap? {
        try {
            context.contentResolver.openInputStream(t)?.let { inputStream ->
                return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream).also {
                    inputStream.close()
                }, 400, 400, true)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}