package com.example.honeycanyoubuythis.database.user

import android.content.Context
import com.example.honeycanyoubuythis.database.AppDatabase

class UserManager private constructor(context: Context) {
    private val currentUserDao = AppDatabase.getInstance(context).currentUserDao()

    suspend fun setUser(email: String, displayName: String) {
        currentUserDao.insert(CurrentUser(email = email, displayName = displayName))
    }

    suspend fun getUserDisplayName(): String? {
        return currentUserDao.getCurrentUser()?.displayName
    }

    suspend fun getUser(): CurrentUser? {
        return currentUserDao.getCurrentUser()
    }

    suspend fun clearUser() {
        currentUserDao.deleteCurrentUser()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserManager? = null
        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
}