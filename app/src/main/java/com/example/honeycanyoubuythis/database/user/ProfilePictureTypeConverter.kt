package com.example.honeycanyoubuythis.database.user

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class ProfilePictureTypeConverter {
    @TypeConverter
    fun fromByteArrayToBitmap(value: ByteArray?): Bitmap? {
        return if (value == null) {
            null
        } else {
            BitmapFactory.decodeByteArray(value, 0, value.size)
        }
    }

    @TypeConverter
    fun fromBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        return if (bitmap == null) {
            null
        } else {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }
    }
}