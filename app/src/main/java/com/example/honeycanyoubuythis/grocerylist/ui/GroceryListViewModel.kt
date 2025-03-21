package com.example.honeycanyoubuythis.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.honeycanyoubuythis.database.groceryList.GroceryItem
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.model.GroceryList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GroceryListViewModel(
    private val groceryListRepository: GroceryListRepository,
    private val inputGroceryList: GroceryList,
) : ViewModel() {
    private val _groceryList = MutableStateFlow<GroceryList>(inputGroceryList)
    val groceryList: StateFlow<GroceryList> = _groceryList.asStateFlow()

    fun addGroceryItem(itemName: String, itemAmount: Int) {
        viewModelScope.launch {
            val groceryItem = GroceryItem(name = itemName, amount = itemAmount, isChecked = false)
            groceryListRepository.addItemToGroceryList(groceryList.value.id, groceryItem)
        }
    }

    fun removeGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryListRepository.removeItemFromGroceryList(groceryList.value.id, item)
        }
    }
}