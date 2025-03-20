package com.example.honeycanyoubuythis.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.honeycanyoubuythis.database.user.CurrentUserDao

class ProfileViewModelFactory(private val currentUserDao: CurrentUserDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(currentUserDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}