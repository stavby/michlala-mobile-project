package com.example.honeycanyoubuythis.grocerylist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.database.user.UserManager
import com.example.honeycanyoubuythis.home.ui.HomeViewModel
import com.example.honeycanyoubuythis.model.GroceryList

class GroceryListViewModelFactory(
    private val groceryListRepository: GroceryListRepository,
    private val currentUserRepository: UserManager,
    private val currentGroceryListId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryListViewModel(
                groceryListRepository,
                currentUserRepository,
                currentGroceryListId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}