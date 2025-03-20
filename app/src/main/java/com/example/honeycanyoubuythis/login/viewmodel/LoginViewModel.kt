package com.example.honeycanyoubuythis.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.database.user.CurrentUserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

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

    suspend fun checkIfLoggedIn(): Boolean  = currentUserDao.getCurrentUser().email.isNotEmpty()

    private suspend fun fetchAndSaveUserData(userId: String) {
        try {
            val documentSnapshot = db.collection("user").document(userId).get().await()
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.data
                val email = userData?.get("email") as? String
                val displayName = userData?.get("displayName") as? String
                val user = email?.let {
                    CurrentUser(
                        id = 1,
                        email = it,
                        displayName = displayName ?: ""
                    )
                }
                if (user != null) {
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
}