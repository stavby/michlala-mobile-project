package com.example.honeycanyoubuythis.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.model.GroceryList

class GroceryListViewModelFactory(
    private val groceryListRepository: GroceryListRepository,
    private val groceryList: GroceryList,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryListViewModel(groceryListRepository, groceryList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}