package com.example.honeycanyoubuythis.profile.viewmodel

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

class ProfileViewModel(private val currentUserDao: CurrentUserDao) : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun checkIfLoggedIn(): Boolean  = currentUserDao.getCurrentUser() != null

    suspend fun getUser(): CurrentUser? = currentUserDao.getCurrentUser()

    suspend fun updateUser(user: CurrentUser): Boolean{
        return try {
            val userId = auth.currentUser?.uid ?: return false

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
}