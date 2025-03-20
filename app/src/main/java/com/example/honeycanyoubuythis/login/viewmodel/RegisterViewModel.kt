package com.example.honeycanyoubuythis.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.database.user.CurrentUserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RegisterViewModel(private val currentUserDao: CurrentUserDao) : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun performSignUp(email: String, password: String, displayName: String): FirebaseUser? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                saveUserToFirestore(it, displayName, email)
                saveCurrentUserToRoom(displayName, email)
            }
            user
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "Error during sign up", e)
            null
        }
    }

    private suspend fun saveUserToFirestore(user: FirebaseUser, displayName: String, email: String) {
        val userRef = db.collection("user").document(user.uid)
        val newUser = com.example.honeycanyoubuythis.model.User(user.uid, displayName, email)
        try {
            userRef.set(newUser).await()
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "Error saving user to Firestore", e)
        }
    }

    private suspend fun saveCurrentUserToRoom(displayName: String, email: String) {
        val currentUser = CurrentUser(displayName = displayName, email = email)
        try {
            currentUserDao.insert(currentUser)
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "Error saving user to Room", e)
        }
    }
}