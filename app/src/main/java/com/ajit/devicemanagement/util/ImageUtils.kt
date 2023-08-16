package com.ajit.devicemanagement.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.ajit.devicemanagement.R
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun loadImageLocally(context: Context, userId: String, imageView: ImageView) {
        val fileName = "$userId.jpg"
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            imageView.setImageBitmap(bitmap)
        } else {
            // File doesn't exist locally, fetch from Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val profilePicRef = storageRef.child("profilePicture/$userId")

            profilePicRef.getFile(file).addOnSuccessListener {
                // Image downloaded successfully, load and display it
                val bitmap = BitmapFactory.decodeFile(file.path)
                imageView.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                // Failed to fetch from Firebase Storage
                Log.e("LoadImageLocally", "Failed to fetch image from Firebase Storage", exception)
            }
        }
    }



    fun saveImageLocally(context: Context, bitmap: Bitmap, userId: String) {
        val fileName = "$userId.jpg"
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
