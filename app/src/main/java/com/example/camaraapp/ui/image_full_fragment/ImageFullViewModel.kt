package com.example.camaraapp.ui.image_full_fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import java.io.ByteArrayOutputStream

class ImageFullViewModel: ViewModel() {
    fun getImageUri(context: Context, image: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
        return Uri.parse(path)
    }

   fun rotateImage(image: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            image, 0, 0, image.width, image.height,
            matrix, true
        )
    }

   fun cropBitMap(image: Bitmap, startX: Int, startY: Int, width: Int, height: Int ) : Bitmap {
        return Bitmap.createBitmap(image, startX, startY, width, height)
    }
}