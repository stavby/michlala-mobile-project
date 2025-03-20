package com.example.honeycanyoubuythis.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.honeycanyoubuythis.database.groceryList.GroceryItem
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.model.GroceryList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val groceryListRepository: GroceryListRepository,
) : ViewModel() {

    private val _groceryLists = MutableStateFlow<List<GroceryList>>(emptyList())
    val groceryLists: StateFlow<List<GroceryList>> = _groceryLists.asStateFlow()

    init {
        fetchGroceryLists()
    }

    private fun fetchGroceryLists() {
        viewModelScope.launch {
            groceryListRepository.getGroceryLists().collect {
                _groceryLists.value = it
            }
        }
    }

    fun addGroceryList(title: String) {
        viewModelScope.launch {
            groceryListRepository.addGroceryList(
                GroceryList(
                    title = title
                )
            )
        }
    }

    fun addGroceryItem(groceryListId: String, itemName: String) {
        viewModelScope.launch {
            groceryListRepository.addItemToGroceryList(
                groceryListId,
                GroceryItem(name = itemName)
            )
        }
    }
}