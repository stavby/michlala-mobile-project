package com.example.honeycanyoubuythis.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository

class HomeViewModelFactory(
    private val groceryListRepository: GroceryListRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(groceryListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}