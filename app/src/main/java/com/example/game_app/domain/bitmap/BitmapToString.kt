package com.example.game_app.domain.bitmap

import android.graphics.Bitmap
import android.util.Base64
import com.example.game_app.data.common.Adapter
import java.io.ByteArrayOutputStream

class BitmapToString : Adapter<Bitmap, String> {
    override fun adapt(t: Bitmap):String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        t.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}