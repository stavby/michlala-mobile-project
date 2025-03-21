package com.example.honeycanyoubuythis.login.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.database.user.CurrentUserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class LoginViewModel(private val currentUserDao: CurrentUserDao) : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun performLogin(email: String, password: String): Boolean {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                fetchAndSaveUserData(it.uid)
            }
            true
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error during login", e)
            false
        }
    }

    suspend fun checkIfLoggedIn(): Boolean = currentUserDao.getCurrentUser() != null

    private suspend fun fetchAndSaveUserData(userId: String) {
        try {
            val documentSnapshot = db.collection("user").document(userId).get().await()
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.data
                val email = userData?.get("email") as? String
                val displayName = userData?.get("displayName") as? String
                val profilePictureUrl = userData?.get("profilePictureUrl") as? String ?: ""
                val profilePicture = getProfilePicture(profilePictureUrl)
                if (email != null && displayName != null) {
                    val user = CurrentUser(
                        id = 1,
                        email = email,
                        displayName = displayName,
                        profilePicture = profilePicture
                    )

                    currentUserDao.insert(user)
                    Log.d("LoginViewModel", "User data fetched and saved successfully.")
                }
            } else {
                Log.e("LoginViewModel", "User document does not exist.")
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error fetching or saving user data: ${e.message}")
        }
    }

    private suspend fun getProfilePicture(profilePictureUrl: String): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        downloadBitmapFromUrl(profilePictureUrl)?.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            byteArrayOutputStream
        )

        return byteArrayOutputStream.toByteArray()
    }

    private suspend fun downloadBitmapFromUrl(imageUrl: String): Bitmap? =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(imageUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    Log.e(
                        "LoginViewModel",
                        "Error downloading profile picture: ${connection.responseCode}"
                    )
                    null
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error downloading profile picture", e)
                null
            } finally {
                connection?.disconnect()
            }
        }
}