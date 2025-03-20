package com.example.honeycanyoubuythis.database.groceryList

import android.util.Log
import com.example.honeycanyoubuythis.model.GroceryList
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroceryListRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    fun getGroceryLists(): Flow<List<GroceryList>> = callbackFlow {
        val listenerRegistration = db.collection("groceryList")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error ->
                if (error != null) {
                    Log.e("GroceryListRepository", "Error getting grocery lists.", error)
                    return@addSnapshotListener
                }

                val groceryLists = mutableListOf<GroceryList>()
                querySnapshot?.let {
                    for (document in it) {
                        val groceryList = document.toObject(GroceryList::class.java)
                        groceryLists.add(groceryList)
                    }
                    trySend(groceryLists).isSuccess
                }

            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun addGroceryList(groceryList: GroceryList) {
        try {
            db.collection("groceryList").add(groceryList).await()
        } catch (e: Exception) {
            Log.e("GroceryListRepository", "Error adding grocery list.", e)
        }
    }

    suspend fun addItemToGroceryList(groceryListId: String, groceryItem: GroceryItem) {
        try {
            val groceryListRef = db.collection("groceryList").document(groceryListId)

            groceryListRef.update(
                "items",
                FieldValue.arrayUnion(groceryItem)
            )
                .await()
        } catch (e: Exception) {
            Log.e("GroceryListRepository", "Error adding item to grocery list.", e)
        }
    }
}