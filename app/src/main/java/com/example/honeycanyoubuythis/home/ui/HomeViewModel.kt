package com.example.honeycanyoubuythis.home.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.honeycanyoubuythis.database.groceryList.GroceryItem
import com.example.honeycanyoubuythis.database.groceryList.GroceryListDao
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.model.GroceryList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val groceryListRepository: GroceryListRepository,
) : ViewModel() {

    private val _groceryLists = MutableStateFlow<List<GroceryList>>(emptyList())
    val groceryLists: StateFlow<List<GroceryList>> = _groceryLists.asStateFlow()
    private val db = Firebase.firestore
    init {
        fetchGroceryLists()
        fetchGroceryListsFromFirebase()
    }

    private fun fetchGroceryLists() {
        viewModelScope.launch {
            groceryListRepository.getGroceryLists().collect {
                _groceryLists.value = it
            }
        }
    }

    private fun fetchGroceryListsFromFirebase() {
        viewModelScope.launch {
            try {
                val result = db.collection("groceryList").get().await()
                val lists = result.toObjects(GroceryList::class.java)
                groceryListRepository.addAllGroceryLists(lists)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching grocery lists from Firebase", e)
            }
        }
    }

    fun addGroceryList(title: String) {
        viewModelScope.launch {
            val list = GroceryList(title = title)
            groceryListRepository.addGroceryList(list)
        }
    }

    fun addGroceryItem(groceryListId: String, itemName: String) {
        viewModelScope.launch {
            val groceryItem = GroceryItem(name = itemName)
            groceryListRepository.addItemToGroceryList(groceryListId, groceryItem)
        }
    }
}