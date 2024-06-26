package com.example.game_app.domain.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.game_app.data.common.Adapter
import java.io.ByteArrayInputStream

class StringToBitmap : Adapter<String, Bitmap> {
    override fun adapt(t: String):Bitmap =
        BitmapFactory
            .decodeStream(ByteArrayInputStream(Base64.decode(t, Base64.DEFAULT)))
}