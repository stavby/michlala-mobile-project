package com.example.honeycanyoubuythis.grocerylist.viewmodel

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
    private val currentGroceryListId: String
) : ViewModel() {
    private val _groceryList = MutableStateFlow(GroceryList())
    val groceryList: StateFlow<GroceryList> = _groceryList.asStateFlow()
    private val db = Firebase.firestore

    init {
        fetchGroceryList()
        fetchGroceryListFromFirebase()
    }

    private fun fetchGroceryList() {
        viewModelScope.launch {
            groceryListRepository.getGroceryList(currentGroceryListId).collect {
                _groceryList.value = it
            }
        }
    }

    private fun fetchGroceryListFromFirebase() {
        viewModelScope.launch {
            try {
                val documentSnapshot =
                    db.collection("groceryList").document(currentGroceryListId).get().await()
                if (documentSnapshot.exists()) {
                    val firebaseGroceryList = documentSnapshot.toObject(GroceryList::class.java)
                    firebaseGroceryList?.let {
                        groceryListRepository.addGroceryList(it)
                    }
                } else {
                    Log.e(
                        "HomeViewModel",
                        "Grocery list with ID $currentGroceryListId not found in Firebase"
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching grocery list from Firebase", e)
            }
        }
    }

    fun addGroceryItem(itemName: String, itemAmount: Int) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()

            val groceryItem =
                GroceryItem(id =id,  name = itemName, amount = itemAmount, isChecked = false)
            groceryListRepository.addItemToGroceryList(currentGroceryListId, groceryItem)
        }
    }

    fun updateGroceryItemCheckedState(item: GroceryItem, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedItem: GroceryItem = item.copy(isChecked = isChecked)
            groceryListRepository.updateGroceryItem(currentGroceryListId, updatedItem)
        }
    }

    fun removeGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryListRepository.removeItemFromGroceryList(currentGroceryListId, item)
        }
    }
}