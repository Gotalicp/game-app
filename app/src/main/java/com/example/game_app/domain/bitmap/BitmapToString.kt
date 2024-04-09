package com.example.game_app.domain.bitmap

import android.graphics.Bitmap
import android.util.Base64
import com.example.game_app.data.common.Adapter
import java.io.ByteArrayOutputStream

class BitmapToString : Adapter<Bitmap, String> {
    override fun adapt(t: Bitmap): String {
        ByteArrayOutputStream().apply {
            t.compress(Bitmap.CompressFormat.PNG, 100, this)
            return Base64.encodeToString(toByteArray(), Base64.DEFAULT)
        }
    }
}