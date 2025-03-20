package com.example.honeycanyoubuythis.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.honeycanyoubuythis.database.user.CurrentUserDao

class LoginViewModelFactory(private val currentUserDao: CurrentUserDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(currentUserDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}