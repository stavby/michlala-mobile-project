package com.example.honeycanyoubuythis.profile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.database.user.CurrentUserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ProfileViewModel(private val currentUserDao: CurrentUserDao) : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private val userId = auth.currentUser?.uid ?: ""

    suspend fun getUser(): CurrentUser? = currentUserDao.getCurrentUser()

    suspend fun updateUser(user: CurrentUser): Boolean {
        return try {
            db.collection("user")
                .document(userId)
                .update("displayName", user.displayName)
                .await()

            currentUserDao.insert(user)

            Log.d("ProfileViewModel", "User display name updated successfully.")
            true
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error updating user display name: ${e.message}")
            false
        }
    }

    suspend fun updateProfilePicture(context: Context, uri: Uri): Boolean {
        val profilePicture = compressImage(context, uri)
        val user = getUser() ?: return false

        return try {
            savePictureToFirebase(userId, profilePicture)
            user.profilePicture = profilePicture
            currentUserDao.insert(user)
            true
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error updating user profile picture: ${e.message}")
            false
        }
    }

    private suspend fun savePictureToFirebase(userId: String, byteArray: ByteArray) {
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_pictures/$userId.png")
        val uploadTask = imageRef.putBytes(byteArray)

        try {
            uploadTask.await()
            val downloadUrl = imageRef.downloadUrl.await()
            val userDocRef = db.collection("user").document(userId)
            val updates = hashMapOf<String, Any>("profilePictureUrl" to downloadUrl)
            userDocRef.update(updates).await()

        } catch (e: Exception) {
            Log.e("Storage", "Error saving the profile picture in storage", e)
        }
    }

    private fun compressImage(context: Context, uri: Uri): ByteArray {
        val bitmap = when (context.contentResolver.getType(uri)?.split("/")?.first()) {
            "image" -> {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            else -> {
                return ByteArray(0)
            }
        }

        val byteArrayOutputStream =
            ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}