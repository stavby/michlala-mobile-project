package com.example.honeycanyoubuythis.database.groceryList

import android.util.Log
import com.example.honeycanyoubuythis.model.GroceryList
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GroceryListRepository(
    private val groceryListDao: GroceryListDao
) {
    private val db: FirebaseFirestore = Firebase.firestore

    fun getGroceryLists(): Flow<List<GroceryList>> =
        groceryListDao.getGroceryLists().map { localGroceryLists ->
            localGroceryLists.map {
                GroceryList(
                    id = it.id,
                    title = it.title,
                    itemCount = it.groceryItems.size,
                    items = it.groceryItems
                )
            }
        }

    suspend fun addGroceryList(groceryList: GroceryList) {
        groceryListDao.addGroceryList(groceryList.toLocalGroceryList())
        try {
            db.collection("groceryList").add(groceryList).await()
        } catch (e: Exception) {
            Log.e("GroceryListRepository", "Error adding grocery list to firebase.", e)
        }
    }

    suspend fun addItemToGroceryList(groceryListId: String, groceryItem: GroceryItem) {
        groceryListDao.addItemToGroceryList(groceryListId, groceryItem)
        try {
            val groceryListRef = db.collection("groceryList").document(groceryListId)

            groceryListRef.update(
                "items",
                FieldValue.arrayUnion(groceryItem)
            ).await()
        } catch (e: Exception) {
            Log.e("GroceryListRepository", "Error adding item to grocery list to firebase.", e)
        }
    }

    //Add multiple grocery lists to room
    suspend fun addAllGroceryLists(groceryLists: List<GroceryList>) {
        val localGroceryLists = groceryLists.map { it.toLocalGroceryList() }
        groceryListDao.addAllGroceryLists(localGroceryLists)
    }
}

// Extension function to convert GroceryList to LocalGroceryList
fun GroceryList.toLocalGroceryList(): LocalGroceryList {
    return LocalGroceryList(
        id = this.id,
        title = this.title,
        groceryItems = this.items
    )
}